package org.xutils.http;

import android.text.TextUtils;

import org.xutils.common.task.Priority;
import org.xutils.common.util.LogUtil;
import org.xutils.http.annotation.HttpRequest;
import org.xutils.http.app.DefaultParamsBuilder;
import org.xutils.http.app.HttpRetryHandler;
import org.xutils.http.app.ParamsBuilder;
import org.xutils.http.app.RedirectHandler;
import org.xutils.http.app.RequestTracker;

import java.net.Proxy;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by wyouflf on 15/7/17.
 * 缃戠粶璇锋眰鍙傛暟瀹炰綋
 */
public class RequestParams extends BaseParams {

    // 娉ㄨВ鍙婂叾鎵╁睍鍙傛暟
    private HttpRequest httpRequest;
    private final String uri;
    private final String[] signs;
    private final String[] cacheKeys;
    private ParamsBuilder builder;
    private String buildUri;
    private String buildCacheKey;
    private SSLSocketFactory sslSocketFactory;

    // 鎵╁睍鍙傛暟
    private Proxy proxy; // 浠ｇ悊
    private boolean useCookie = true; // 鏄惁鍦ㄨ姹傝繃绋嬩腑鍚敤cookie
    private String cacheDirName; // 缂撳瓨鏂囦欢澶瑰悕绉�   
    private long cacheSize; // 缂撳瓨鏂囦欢澶瑰ぇ灏�    
    private long cacheMaxAge; // 榛樿缂撳瓨瀛樻椿鏃堕棿, 鍗曚綅:姣.(濡傛灉鏈嶅姟娌℃湁杩斿洖鏈夋晥鐨刴ax-age鎴朎xpires)
    private Executor executor; // 鑷畾涔夌嚎绋嬫睜
    private Priority priority = Priority.DEFAULT; // 璇锋眰浼樺厛绾�    
    private int connectTimeout = 1000 * 15; // 杩炴帴瓒呮椂鏃堕棿
    private boolean autoResume = true; // 鏄惁鍦ㄤ笅杞芥槸鑷姩鏂偣缁紶
    private boolean autoRename = false; // 鏄惁鏍规嵁澶翠俊鎭嚜鍔ㄥ懡鍚嶆枃浠�    
    private int maxRetryCount = 2; // 鏈�ぇ璇锋眰閿欒閲嶈瘯娆℃暟
    private String saveFilePath; // 涓嬭浇鏂囦欢鏃舵枃浠朵繚瀛樼殑璺緞鍜屾枃浠跺悕
    private boolean cancelFast = false; // 鏄惁鍙互琚珛鍗冲仠姝� true: 涓鸿姹傚垱寤烘柊鐨勭嚎绋� 鍙栨秷鏃惰姹傜嚎绋嬭绔嬪嵆涓柇.
    private int loadingUpdateMaxTimeSpan = 300; // 杩涘害鍒锋柊鏈�ぇ闂撮殧鏃堕棿(ms)
    private HttpRetryHandler httpRetryHandler; // 鑷畾涔塇ttpRetryHandler
    private RedirectHandler redirectHandler; // 鑷畾涔夐噸瀹氬悜鎺ュ彛, 榛樿绯荤粺鑷姩閲嶅畾鍚�
    private RequestTracker requestTracker; // 鑷畾涔夋棩蹇楄褰曟帴鍙�

    /**
     * 浣跨敤绌烘瀯閫犲垱寤烘椂蹇呴』, 蹇呴』鏄甫鏈堾HttpRequest娉ㄨВ鐨勫瓙绫�
     */
    public RequestParams() {
        this(null, null, null, null);
    }

    /**
     * @param uri 涓嶅彲涓虹┖
     */
    public RequestParams(String uri) {
        this(uri, null, null, null);
    }

    /**
     * @param uri       涓嶅彲涓虹┖
     * @param builder
     * @param signs
     * @param cacheKeys
     */
    public RequestParams(String uri, ParamsBuilder builder, String[] signs, String[] cacheKeys) {
        if (uri != null && builder == null) {
            builder = new DefaultParamsBuilder();
        }
        this.uri = uri;
        this.signs = signs;
        this.cacheKeys = cacheKeys;
        this.builder = builder;
    }

    // invoke via HttpTask#createNewRequest
    /*package*/ void init() throws Throwable {
        if (!TextUtils.isEmpty(buildUri)) return;

        if (TextUtils.isEmpty(uri) && getHttpRequest() == null) {
            throw new IllegalStateException("uri is empty && @HttpRequest == null");
        }

        // init params from entity
        initEntityParams();

        // build uri & cacheKey
        buildUri = uri;
        HttpRequest httpRequest = this.getHttpRequest();
        if (httpRequest != null) {
            builder = httpRequest.builder().newInstance();
            buildUri = builder.buildUri(this, httpRequest);
            builder.buildParams(this);
            builder.buildSign(this, httpRequest.signs());
            if (sslSocketFactory == null) {
                sslSocketFactory = builder.getSSLSocketFactory();
            }
        } else if (this.builder != null) {
            builder.buildParams(this);
            builder.buildSign(this, signs);
            if (sslSocketFactory == null) {
                sslSocketFactory = builder.getSSLSocketFactory();
            }
        }
    }

    public String getUri() {
        return TextUtils.isEmpty(buildUri) ? uri : buildUri;
    }

    public String getCacheKey() {
        if (TextUtils.isEmpty(buildCacheKey) && builder != null) {
            HttpRequest httpRequest = this.getHttpRequest();
            if (httpRequest != null) {
                buildCacheKey = builder.buildCacheKey(this, httpRequest.cacheKeys());
            } else {
                buildCacheKey = builder.buildCacheKey(this, cacheKeys);
            }
        }
        return buildCacheKey;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    /**
     * 鏄惁鍦ㄨ姹傝繃绋嬩腑鍚敤cookie, 榛樿true.
     *
     * @return
     */
    public boolean isUseCookie() {
        return useCookie;
    }

    /**
     * 鏄惁鍦ㄨ姹傝繃绋嬩腑鍚敤cookie, 榛樿true.
     *
     * @param useCookie
     */
    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout > 0) {
            this.connectTimeout = connectTimeout;
        }
    }

    public String getCacheDirName() {
        return cacheDirName;
    }

    public void setCacheDirName(String cacheDirName) {
        this.cacheDirName = cacheDirName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * 榛樿缂撳瓨瀛樻椿鏃堕棿, 鍗曚綅:姣.(濡傛灉鏈嶅姟娌℃湁杩斿洖鏈夋晥鐨刴ax-age鎴朎xpires)
     *
     * @return
     */
    public long getCacheMaxAge() {
        return cacheMaxAge;
    }

    /**
     * 榛樿缂撳瓨瀛樻椿鏃堕棿, 鍗曚綅:姣.(濡傛灉鏈嶅姟娌℃湁杩斿洖鏈夋晥鐨刴ax-age鎴朎xpires)
     *
     * @param cacheMaxAge
     */
    public void setCacheMaxAge(long cacheMaxAge) {
        this.cacheMaxAge = cacheMaxAge;
    }

    /**
     * 鑷畾涔夌嚎绋嬫睜
     *
     * @return
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * 鑷畾涔夌嚎绋嬫睜
     *
     * @param executor
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * 鏄惁鍦ㄤ笅杞芥槸鑷姩鏂偣缁紶
     */
    public boolean isAutoResume() {
        return autoResume;
    }

    /**
     * 璁剧疆鏄惁鍦ㄤ笅杞芥槸鑷姩鏂偣缁紶
     *
     * @param autoResume
     */
    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
    }

    /**
     * 鏄惁鏍规嵁澶翠俊鎭嚜鍔ㄥ懡鍚嶆枃浠�     */
    public boolean isAutoRename() {
        return autoRename;
    }

    /**
     * 璁剧疆鏄惁鏍规嵁澶翠俊鎭嚜鍔ㄥ懡鍚嶆枃浠�     *
     * @param autoRename
     */
    public void setAutoRename(boolean autoRename) {
        this.autoRename = autoRename;
    }

    /**
     * 鑾峰彇涓嬭浇鏂囦欢鏃舵枃浠朵繚瀛樼殑璺緞鍜屾枃浠跺悕
     */
    public String getSaveFilePath() {
        return saveFilePath;
    }

    /**
     * 璁剧疆涓嬭浇鏂囦欢鏃舵枃浠朵繚瀛樼殑璺緞鍜屾枃浠跺悕
     *
     * @param saveFilePath
     */
    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * 鏄惁鍙互琚珛鍗冲仠姝�
     *
     * @return true: 涓鸿姹傚垱寤烘柊鐨勭嚎绋� 鍙栨秷鏃惰姹傜嚎绋嬭绔嬪嵆涓柇; false: 璇锋眰寤虹珛杩囩▼鍙兘涓嶈绔嬪嵆缁堟.
     */
    public boolean isCancelFast() {
        return cancelFast;
    }

    /**
     * 鏄惁鍙互琚珛鍗冲仠姝�
     *
     * @param cancelFast true: 涓鸿姹傚垱寤烘柊鐨勭嚎绋� 鍙栨秷鏃惰姹傜嚎绋嬭绔嬪嵆涓柇; false: 璇锋眰寤虹珛杩囩▼鍙兘涓嶈绔嬪嵆缁堟.
     */
    public void setCancelFast(boolean cancelFast) {
        this.cancelFast = cancelFast;
    }

    public int getLoadingUpdateMaxTimeSpan() {
        return loadingUpdateMaxTimeSpan;
    }

    /**
     * 杩涘害鍒锋柊鏈�ぇ闂撮殧鏃堕棿(榛樿300姣)
     *
     * @param loadingUpdateMaxTimeSpan
     */
    public void setLoadingUpdateMaxTimeSpan(int loadingUpdateMaxTimeSpan) {
        this.loadingUpdateMaxTimeSpan = loadingUpdateMaxTimeSpan;
    }

    public HttpRetryHandler getHttpRetryHandler() {
        return httpRetryHandler;
    }

    public void setHttpRetryHandler(HttpRetryHandler httpRetryHandler) {
        this.httpRetryHandler = httpRetryHandler;
    }

    public RedirectHandler getRedirectHandler() {
        return redirectHandler;
    }

    /**
     * 鑷畾涔夐噸瀹氬悜鎺ュ彛, 榛樿绯荤粺鑷姩閲嶅畾鍚�
     *
     * @param redirectHandler
     */
    public void setRedirectHandler(RedirectHandler redirectHandler) {
        this.redirectHandler = redirectHandler;
    }

    public RequestTracker getRequestTracker() {
        return requestTracker;
    }

    public void setRequestTracker(RequestTracker requestTracker) {
        this.requestTracker = requestTracker;
    }

    private void initEntityParams() {
        RequestParamsHelper.parseKV(this, this.getClass(), new RequestParamsHelper.ParseKVListener() {
            @Override
            public void onParseKV(String name, Object value) {
                addParameter(name, value);
            }
        });
    }

    private boolean invokedGetHttpRequest = false;

    private HttpRequest getHttpRequest() {
        if (httpRequest == null && !invokedGetHttpRequest) {
            invokedGetHttpRequest = true;
            Class<?> thisCls = this.getClass();
            if (thisCls != RequestParams.class) {
                httpRequest = thisCls.getAnnotation(HttpRequest.class);
            }
        }

        return httpRequest;
    }

    /**
     * 鍦ㄧ綉缁滆姹俹nStart鍓� 灏介噺涓嶈鍦║I绾跨▼璋冪敤杩欎釜鏂规硶, 鍙兘浜х敓鎬ц兘褰卞搷.
     *
     * @return
     */
    @Override
    public String toString() {
        try {
            this.init();
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
        String url = this.getUri();
        return TextUtils.isEmpty(url) ?
                super.toString() :
                url + (url.contains("?") ? "&" : "?") + super.toString();
    }
}
