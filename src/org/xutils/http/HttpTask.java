package org.xutils.http;

import android.text.TextUtils;

import org.xutils.common.Callback;
import org.xutils.common.task.AbsTask;
import org.xutils.common.task.Priority;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.IOUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.common.util.ParameterizedTypeUtil;
import org.xutils.ex.HttpException;
import org.xutils.ex.HttpRedirectException;
import org.xutils.http.app.HttpRetryHandler;
import org.xutils.http.app.RedirectHandler;
import org.xutils.http.app.RequestInterceptListener;
import org.xutils.http.app.RequestTracker;
import org.xutils.http.request.UriRequest;
import org.xutils.http.request.UriRequestFactory;
import org.xutils.x;

import java.io.Closeable;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wyouflf on 15/7/23.
 * http 璇锋眰浠诲姟
 */
public class HttpTask<ResultType> extends AbsTask<ResultType> implements ProgressHandler {

    // 璇锋眰鐩稿叧
    private RequestParams params;
    private UriRequest request;
    private RequestWorker requestWorker;
    private final Executor executor;
    private volatile boolean hasException = false;
    private final Callback.CommonCallback<ResultType> callback;

    // 缂撳瓨鎺у埗
    private Object rawResult = null;
    private volatile Boolean trustCache = null;
    private final Object cacheLock = new Object();

    // 鎵╁睍callback
    private Callback.CacheCallback<ResultType> cacheCallback;
    private Callback.PrepareCallback prepareCallback;
    private Callback.ProgressCallback progressCallback;
    private RequestInterceptListener requestInterceptListener;

    // 鏃ュ織杩借釜
    private RequestTracker tracker;

    // 鏂囦欢涓嬭浇绾跨▼鏁伴檺鍒�   
    private Type loadType;
    private final static int MAX_FILE_LOAD_WORKER = 3;
    private final static AtomicInteger sCurrFileLoadCount = new AtomicInteger(0);

    // 鏂囦欢涓嬭浇浠诲姟
    private static final HashMap<String, WeakReference<HttpTask<?>>>
            DOWNLOAD_TASK = new HashMap<String, WeakReference<HttpTask<?>>>(1);

    private static final PriorityExecutor HTTP_EXECUTOR = new PriorityExecutor(5, true);
    private static final PriorityExecutor CACHE_EXECUTOR = new PriorityExecutor(5, true);


    public HttpTask(RequestParams params, Callback.Cancelable cancelHandler,
                    Callback.CommonCallback<ResultType> callback) {
        super(cancelHandler);

        assert params != null;
        assert callback != null;

        // set params & callback
        this.params = params;
        this.callback = callback;
        if (callback instanceof Callback.CacheCallback) {
            this.cacheCallback = (Callback.CacheCallback<ResultType>) callback;
        }
        if (callback instanceof Callback.PrepareCallback) {
            this.prepareCallback = (Callback.PrepareCallback) callback;
        }
        if (callback instanceof Callback.ProgressCallback) {
            this.progressCallback = (Callback.ProgressCallback<ResultType>) callback;
        }
        if (callback instanceof RequestInterceptListener) {
            this.requestInterceptListener = (RequestInterceptListener) callback;
        }

        // init tracker
        {
            RequestTracker customTracker = params.getRequestTracker();
            if (customTracker == null) {
                if (callback instanceof RequestTracker) {
                    customTracker = (RequestTracker) callback;
                } else {
                    customTracker = UriRequestFactory.getDefaultTracker();
                }
            }
            if (customTracker != null) {
                tracker = new RequestTrackerWrapper(customTracker);
            }
        }

        // init executor
        if (params.getExecutor() != null) {
            this.executor = params.getExecutor();
        } else {
            if (cacheCallback != null) {
                this.executor = CACHE_EXECUTOR;
            } else {
                this.executor = HTTP_EXECUTOR;
            }
        }
    }

    // 瑙ｆ瀽loadType
    private void resolveLoadType() {
        Class<?> callBackType = callback.getClass();
        if (callback instanceof Callback.TypedCallback) {
            loadType = ((Callback.TypedCallback) callback).getLoadType();
        } else if (callback instanceof Callback.PrepareCallback) {
            loadType = ParameterizedTypeUtil.getParameterizedType(callBackType, Callback.PrepareCallback.class, 0);
        } else {
            loadType = ParameterizedTypeUtil.getParameterizedType(callBackType, Callback.CommonCallback.class, 0);
        }
    }

    // 鍒濆鍖栬姹傚弬鏁�    
    private UriRequest createNewRequest() throws Throwable {
        // init request
        params.init();
        UriRequest result = UriRequestFactory.getUriRequest(params, loadType);
        result.setCallingClassLoader(callback.getClass().getClassLoader());
        result.setProgressHandler(this);
        this.loadingUpdateMaxTimeSpan = params.getLoadingUpdateMaxTimeSpan();
        this.update(FLAG_REQUEST_CREATED, result);
        return result;
    }

    // 鏂囦欢涓嬭浇鍐茬獊妫�祴
    private void checkDownloadTask() {
        if (File.class == loadType) {
            synchronized (DOWNLOAD_TASK) {
                String downloadTaskKey = this.params.getSaveFilePath();
                /*{
                    // 涓嶅鐞嗙紦瀛樻枃浠朵笅杞藉啿绐�
                    // 缂撳瓨鏂囦欢涓嬭浇鍐茬獊浼氭姏鍑篎ileLockedException寮傚父,
                    // 浣跨敤寮傚父澶勭悊鎺у埗鏄惁閲嶆柊灏濊瘯涓嬭浇.
                    if (TextUtils.isEmpty(downloadTaskKey)) {
                        downloadTaskKey = this.request.getCacheKey();
                    }
                }*/
                if (!TextUtils.isEmpty(downloadTaskKey)) {
                    WeakReference<HttpTask<?>> taskRef = DOWNLOAD_TASK.get(downloadTaskKey);
                    if (taskRef != null) {
                        HttpTask<?> task = taskRef.get();
                        if (task != null) {
                            task.cancel();
                            task.closeRequestSync();
                        }
                        DOWNLOAD_TASK.remove(downloadTaskKey);
                    }
                    DOWNLOAD_TASK.put(downloadTaskKey, new WeakReference<HttpTask<?>>(this));
                } // end if (!TextUtils.isEmpty(downloadTaskKey))

                if (DOWNLOAD_TASK.size() > MAX_FILE_LOAD_WORKER) {
                    Iterator<Map.Entry<String, WeakReference<HttpTask<?>>>>
                            entryItr = DOWNLOAD_TASK.entrySet().iterator();
                    while (entryItr.hasNext()) {
                        Map.Entry<String, WeakReference<HttpTask<?>>> next = entryItr.next();
                        WeakReference<HttpTask<?>> value = next.getValue();
                        if (value == null || value.get() == null) {
                            entryItr.remove();
                        }
                    }
                }
            } // end synchronized
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ResultType doBackground() throws Throwable {

        if (this.isCancelled()) {
            throw new Callback.CancelledException("cancelled before request");
        }

        // 鍒濆鍖栬姹傚弬鏁�        
        ResultType result = null;
        resolveLoadType();
        request = createNewRequest();
        checkDownloadTask();
        // retry 鍒濆鍖�        
        boolean retry = true;
        int retryCount = 0;
        Throwable exception = null;
        HttpRetryHandler retryHandler = this.params.getHttpRetryHandler();
        if (retryHandler == null) {
            retryHandler = new HttpRetryHandler();
        }
        retryHandler.setMaxRetryCount(this.params.getMaxRetryCount());

        if (this.isCancelled()) {
            throw new Callback.CancelledException("cancelled before request");
        }

        // 妫�煡缂撳瓨
        Object cacheResult = null;
        if (cacheCallback != null && HttpMethod.permitsCache(params.getMethod())) {
            // 灏濊瘯浠庣紦瀛樿幏鍙栫粨鏋� 骞朵负璇锋眰澶村姞鍏ョ紦瀛樻帶鍒跺弬鏁�
            try {
                clearRawResult();
                LogUtil.d("load cache: " + this.request.getRequestUri());
                rawResult = this.request.loadResultFromCache();
            } catch (Throwable ex) {
                LogUtil.w("load disk cache error", ex);
            }

            if (this.isCancelled()) {
                clearRawResult();
                throw new Callback.CancelledException("cancelled before request");
            }

            if (rawResult != null) {
                if (prepareCallback != null) {
                    try {
                        cacheResult = prepareCallback.prepare(rawResult);
                    } catch (Throwable ex) {
                        cacheResult = null;
                        LogUtil.w("prepare disk cache error", ex);
                    } finally {
                        clearRawResult();
                    }
                } else {
                    cacheResult = rawResult;
                }

                if (this.isCancelled()) {
                    throw new Callback.CancelledException("cancelled before request");
                }

                if (cacheResult != null) {
                    // 鍚屾绛夊緟鏄惁淇′换缂撳瓨
                    this.update(FLAG_CACHE, cacheResult);
                    while (trustCache == null) {
                        synchronized (cacheLock) {
                            try {
                                cacheLock.wait();
                            } catch (InterruptedException iex) {
                                throw new Callback.CancelledException("cancelled before request");
                            } catch (Throwable ignored) {
                            }
                        }
                    }

                    // 澶勭悊瀹屾垚
                    if (trustCache) {
                        return null;
                    }
                }
            }
        }

        if (trustCache == null) {
            trustCache = false;
        }

        if (cacheResult == null) {
            this.request.clearCacheHeader();
        }

        // 鍒ゆ柇璇锋眰鐨勭紦瀛樼瓥鐣�        
        if (callback instanceof Callback.ProxyCacheCallback) {
            if (((Callback.ProxyCacheCallback) callback).onlyCache()) {
                return null;
            }
        }

        // 鍙戣捣璇锋眰
        retry = true;
        while (retry) {
            retry = false;

            try {
                if (this.isCancelled()) {
                    throw new Callback.CancelledException("cancelled before request");
                }

                // 鐢眑oader鍙戣捣璇锋眰, 鎷垮埌缁撴灉.
                this.request.close(); // retry 鍓嶅叧闂笂娆¤姹�
                try {
                    clearRawResult();
                    // 寮�璇锋眰宸ヤ綔
                    LogUtil.d("load: " + this.request.getRequestUri());
                    requestWorker = new RequestWorker();
                    if (params.isCancelFast()) {
                        requestWorker.start();
                        requestWorker.join();
                    } else {
                        requestWorker.run();
                    }
                    if (requestWorker.ex != null) {
                        throw requestWorker.ex;
                    }
                    rawResult = requestWorker.result;
                } catch (Throwable ex) {
                    clearRawResult();
                    if (this.isCancelled()) {
                        throw new Callback.CancelledException("cancelled during request");
                    } else {
                        throw ex;
                    }
                }

                if (prepareCallback != null) {

                    if (this.isCancelled()) {
                        throw new Callback.CancelledException("cancelled before request");
                    }

                    try {
                        result = (ResultType) prepareCallback.prepare(rawResult);
                    } finally {
                        clearRawResult();
                    }
                } else {
                    result = (ResultType) rawResult;
                }

                // 淇濆瓨缂撳瓨
                if (cacheCallback != null && HttpMethod.permitsCache(params.getMethod())) {
                    this.request.save2Cache();
                }

                if (this.isCancelled()) {
                    throw new Callback.CancelledException("cancelled after request");
                }
            } catch (HttpRedirectException redirectEx) {
                retry = true;
                LogUtil.w("Http Redirect:" + params.getUri());
            } catch (Throwable ex) {
                switch (this.request.getResponseCode()) {
                    case 204: // empty content
                    case 205: // empty content
                    case 304: // disk cache is valid.
                        return null;
                    default: {
                        exception = ex;
                        if (this.isCancelled() && !(exception instanceof Callback.CancelledException)) {
                            exception = new Callback.CancelledException("canceled by user");
                        }
                        retry = retryHandler.canRetry(this.request, exception, ++retryCount);
                    }
                }
            }

        }

        if (exception != null && result == null && !trustCache) {
            hasException = true;
            throw exception;
        }

        return result;
    }

    private static final int FLAG_REQUEST_CREATED = 1;
    private static final int FLAG_CACHE = 2;
    private static final int FLAG_PROGRESS = 3;

    @Override
    @SuppressWarnings("unchecked")
    protected void onUpdate(int flag, Object... args) {
        switch (flag) {
            case FLAG_REQUEST_CREATED: {
                if (this.tracker != null) {
                    this.tracker.onRequestCreated((UriRequest) args[0]);
                }
                break;
            }
            case FLAG_CACHE: {
                synchronized (cacheLock) {
                    try {
                        ResultType result = (ResultType) args[0];
                        if (tracker != null) {
                            tracker.onCache(request, result);
                        }
                        trustCache = this.cacheCallback.onCache(result);
                    } catch (Throwable ex) {
                        trustCache = false;
                        callback.onError(ex, true);
                    } finally {
                        cacheLock.notifyAll();
                    }
                }
                break;
            }
            case FLAG_PROGRESS: {
                if (this.progressCallback != null && args.length == 3) {
                    try {
                        this.progressCallback.onLoading(
                                ((Number) args[0]).longValue(),
                                ((Number) args[1]).longValue(),
                                (Boolean) args[2]);
                    } catch (Throwable ex) {
                        callback.onError(ex, true);
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onWaiting() {
        if (tracker != null) {
            tracker.onWaiting(params);
        }
        if (progressCallback != null) {
            progressCallback.onWaiting();
        }
    }

    @Override
    protected void onStarted() {
        if (tracker != null) {
            tracker.onStart(params);
        }
        if (progressCallback != null) {
            progressCallback.onStarted();
        }
    }

    @Override
    protected void onSuccess(ResultType result) {
        if (hasException) return;
        if (tracker != null) {
            tracker.onSuccess(request, result);
        }
        callback.onSuccess(result);
    }

    @Override
    protected void onError(Throwable ex, boolean isCallbackError) {
        if (tracker != null) {
            tracker.onError(request, ex, isCallbackError);
        }
        callback.onError(ex, isCallbackError);
    }


    @Override
    protected void onCancelled(Callback.CancelledException cex) {
        if (tracker != null) {
            tracker.onCancelled(request);
        }
        callback.onCancelled(cex);
    }

    @Override
    protected void onFinished() {
        if (tracker != null) {
            tracker.onFinished(request);
        }
        x.task().run(new Runnable() {
            @Override
            public void run() {
                closeRequestSync();
            }
        });
        callback.onFinished();
    }

    private void clearRawResult() {
        if (rawResult instanceof Closeable) {
            IOUtil.closeQuietly((Closeable) rawResult);
        }
        rawResult = null;
    }

    @Override
    protected void cancelWorks() {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                closeRequestSync();
            }
        });
    }

    @Override
    protected boolean isCancelFast() {
        return params.isCancelFast();
    }

    private void closeRequestSync() {
        clearRawResult();
        if (requestWorker != null && params.isCancelFast()) {
            try {
                requestWorker.interrupt();
            } catch (Throwable ignored) {
            }
        }
        // wtf: okhttp close the inputStream be locked by BufferedInputStream#read
        IOUtil.closeQuietly(request);
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public Priority getPriority() {
        return params.getPriority();
    }

    // ############################### start: region implements ProgressHandler
    private long lastUpdateTime;
    private long loadingUpdateMaxTimeSpan = 300; // 300ms

    /**
     * @param total
     * @param current
     * @param forceUpdateUI
     * @return continue
     */
    @Override
    public boolean updateProgress(long total, long current, boolean forceUpdateUI) {

        if (isCancelled() || isFinished()) {
            return false;
        }

        if (progressCallback != null && request != null && total > 0) {
            if (total < current) {
                total = current;
            }
            if (forceUpdateUI) {
                lastUpdateTime = System.currentTimeMillis();
                this.update(FLAG_PROGRESS, total, current, request.isLoading());
            } else {
                long currTime = System.currentTimeMillis();
                if (currTime - lastUpdateTime >= loadingUpdateMaxTimeSpan) {
                    lastUpdateTime = currTime;
                    this.update(FLAG_PROGRESS, total, current, request.isLoading());
                }
            }
        }

        return !isCancelled() && !isFinished();
    }

    // ############################### end: region implements ProgressHandler

    @Override
    public String toString() {
        return params.toString();
    }


    /**
     * 璇锋眰鍙戦�鍜屽姞杞芥暟鎹嚎绋�
     * 璇ョ嚎绋嬭join鍒癏ttpTask鐨勫伐浣滅嚎绋嬪幓鎵ц.
     * 瀹冪殑涓昏浣滅敤鏄负浜嗚兘寮鸿涓柇璇锋眰鐨勯摼鎺ヨ繃绋�
     * 骞惰緟鍔╅檺鍒跺悓鏃朵笅杞芥枃浠剁殑绾跨▼鏁�
     * but:
     * 鍒涘缓涓�釜Thread绾﹁�鏃�姣, 浼樺寲?
     */
    private final class RequestWorker extends Thread {
        /*private*/ Object result;
        /*private*/ Throwable ex;

        private RequestWorker() {
        }

        public void run() {
            try {
                boolean interrupted = false;
                if (File.class == loadType) {
                    while (sCurrFileLoadCount.get() >= MAX_FILE_LOAD_WORKER
                            && !HttpTask.this.isCancelled()) {
                        synchronized (sCurrFileLoadCount) {
                            try {
                                sCurrFileLoadCount.wait();
                            } catch (InterruptedException iex) {
                                interrupted = true;
                                break;
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                    sCurrFileLoadCount.incrementAndGet();
                }

                if (interrupted || HttpTask.this.isCancelled()) {
                    throw new Callback.CancelledException("cancelled before request" + (interrupted ? "(interrupted)" : ""));
                }

                // intercept response
                if (requestInterceptListener != null) {
                    requestInterceptListener.beforeRequest(request);
                }

                try {
                    this.result = request.loadResult();
                } catch (Throwable ex) {
                    this.ex = ex;
                }

                // intercept response
                if (requestInterceptListener != null) {
                    requestInterceptListener.afterRequest(request);
                }

                if (this.ex != null) {
                    throw this.ex;
                }
            } catch (Throwable ex) {
                this.ex = ex;
                if (ex instanceof HttpException) {
                    HttpException httpEx = (HttpException) ex;
                    int errorCode = httpEx.getCode();
                    if (errorCode == 301 || errorCode == 302) {
                        RedirectHandler redirectHandler = params.getRedirectHandler();
                        if (redirectHandler != null) {
                            try {
                                RequestParams redirectParams = redirectHandler.getRedirectParams(request);
                                if (redirectParams != null) {
                                    if (redirectParams.getMethod() == null) {
                                        redirectParams.setMethod(params.getMethod());
                                    }
                                    // 寮�閲嶅畾鍚戣姹�                                    HttpTask.this.params = redirectParams;
                                    HttpTask.this.request = createNewRequest();
                                    this.ex = new HttpRedirectException(errorCode, httpEx.getMessage(), httpEx.getResult());
                                }
                            } catch (Throwable throwable) {
                                this.ex = ex;
                            }
                        }
                    }
                }
            } finally {
                if (File.class == loadType) {
                    synchronized (sCurrFileLoadCount) {
                        sCurrFileLoadCount.decrementAndGet();
                        sCurrFileLoadCount.notifyAll();
                    }
                }
            }
        }
    }

}
