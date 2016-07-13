package org.xutils.http.app;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.annotation.HttpRequest;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by wyouflf on 15/8/20.
 * 榛樿鍙傛暟鏋勯�鍣� */
public class DefaultParamsBuilder implements ParamsBuilder {

    public DefaultParamsBuilder() {
    }

    /**
     * 鏍规嵁@HttpRequest鏋勫缓璇锋眰鐨剈rl
     *
     * @param params
     * @param httpRequest
     * @return
     */
    @Override
    public String buildUri(RequestParams params, HttpRequest httpRequest) {
        return httpRequest.host() + "/" + httpRequest.path();
    }

    /**
     * 鏍规嵁娉ㄨВ鐨刢acheKeys鏋勫缓缂撳瓨鐨勮嚜瀹氫箟key,
     * 濡傛灉杩斿洖null, 榛樿浣跨敤 url 鍜屾暣涓�query string 缁勬垚.
     *
     * @param params
     * @param cacheKeys
     * @return
     */
    @Override
    public String buildCacheKey(RequestParams params, String[] cacheKeys) {
        String cacheKey = null;
        if (cacheKeys != null && cacheKeys.length > 0) {

            cacheKey = params.getUri() + "?";

            // 娣诲姞cacheKeys瀵瑰簲鐨勫弬鏁�           
            for (String key : cacheKeys) {
                String value = params.getStringParameter(key);
                if (value != null) {
                    cacheKey += key + "=" + value + "&";
                }
            }
        }
        return cacheKey;
    }

    /**
     * 鑷畾涔塖SLSocketFactory
     *
     * @return
     */
    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return getTrustAllSSLSocketFactory();
    }

    /**
     * 涓鸿姹傛坊鍔犻�鐢ㄥ弬鏁扮瓑鎿嶄綔
     *
     * @param params
     */
    @Override
    public void buildParams(RequestParams params) {
    }

    /**
     * 鑷畾涔夊弬鏁扮鍚�     *
     * @param params
     * @param signs
     */
    @Override
    public void buildSign(RequestParams params, String[] signs) {

    }

    private static SSLSocketFactory trustAllSSlSocketFactory;

    public static SSLSocketFactory getTrustAllSSLSocketFactory() {
        if (trustAllSSlSocketFactory == null) {
            synchronized (DefaultParamsBuilder.class) {
                if (trustAllSSlSocketFactory == null) {

                    // 淇′换鎵�湁璇佷功
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }};
                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCerts, null);
                        trustAllSSlSocketFactory = sslContext.getSocketFactory();
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        }

        return trustAllSSlSocketFactory;
    }

}
