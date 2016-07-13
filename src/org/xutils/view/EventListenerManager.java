/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xutils.view;

import android.text.TextUtils;
import android.view.View;

import org.xutils.common.util.DoubleKeyValueMap;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.Event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*package*/ final class EventListenerManager {

    private final static long QUICK_EVENT_TIME_SPAN = 300;
    private final static HashSet<String> AVOID_QUICK_EVENT_SET = new HashSet<String>(2);

    static {
        AVOID_QUICK_EVENT_SET.add("onClick");
        AVOID_QUICK_EVENT_SET.add("onItemClick");
    }

    private EventListenerManager() {
    }

    /**
     * k1: viewInjectInfo
     * k2: interface Type
     * value: listener
     */
    private final static DoubleKeyValueMap<ViewInfo, Class<?>, Object>
            listenerCache = new DoubleKeyValueMap<ViewInfo, Class<?>, Object>();


    public static void addEventMethod(
            //鏍规嵁椤甸潰鎴杤iew holder鐢熸垚鐨刅iewFinder
            ViewFinder finder,
            //鏍规嵁褰撳墠娉ㄨВID鐢熸垚鐨刅iewInfo
            ViewInfo info,
            //娉ㄨВ瀵硅薄
            Event event,
            //椤甸潰鎴杤iew holder瀵硅薄
            Object handler,
            //褰撳墠娉ㄨВ鏂规硶
            Method method) {
        try {
            View view = finder.findViewByInfo(info);

            if (view != null) {
                // 娉ㄨВ涓畾涔夌殑鎺ュ彛锛屾瘮濡侲vent娉ㄨВ榛樿鐨勬帴鍙ｄ负View.OnClickListener
                Class<?> listenerType = event.type();
                // 榛樿涓虹┖锛屾敞瑙ｆ帴鍙ｅ搴旂殑Set鏂规硶锛屾瘮濡俿etOnClickListener鏂规硶
                String listenerSetter = event.setter();
                if (TextUtils.isEmpty(listenerSetter)) {
                    listenerSetter = "set" + listenerType.getSimpleName();
                }


                String methodName = event.method();

                boolean addNewMethod = false;
                /*
                    鏍规嵁View鐨処D鍜屽綋鍓嶇殑鎺ュ彛绫诲瀷鑾峰彇宸茬粡缂撳瓨鐨勬帴鍙ｅ疄渚嬪璞★紝
                    姣斿鏍规嵁View.id鍜孷iew.OnClickListener.class涓や釜閿幏鍙栬繖涓猇iew鐨凮nClickListener瀵硅薄
                 */
                Object listener = listenerCache.get(info, listenerType);
                DynamicHandler dynamicHandler = null;
                /*
                    濡傛灉鎺ュ彛瀹炰緥瀵硅薄涓嶄负绌�                    鑾峰彇鎺ュ彛瀵硅薄瀵瑰簲鐨勫姩鎬佷唬鐞嗗璞�                    濡傛灉鍔ㄦ�浠ｇ悊瀵硅薄鐨刪andler鍜屽綋鍓峢andler鐩稿悓
                    鍒欎负鍔ㄦ�浠ｇ悊瀵硅薄娣诲姞浠ｇ悊鏂规硶
                 */
                if (listener != null) {
                    dynamicHandler = (DynamicHandler) Proxy.getInvocationHandler(listener);
                    addNewMethod = handler.equals(dynamicHandler.getHandler());
                    if (addNewMethod) {
                        dynamicHandler.addMethod(methodName, method);
                    }
                }

                // 濡傛灉杩樻病鏈夋敞鍐屾浠ｇ悊
                if (!addNewMethod) {

                    dynamicHandler = new DynamicHandler(handler);

                    dynamicHandler.addMethod(methodName, method);

                    // 鐢熸垚鐨勪唬鐞嗗璞″疄渚嬶紝姣斿View.OnClickListener鐨勫疄渚嬪璞�                   
                    listener = Proxy.newProxyInstance(
                            listenerType.getClassLoader(),
                            new Class<?>[]{listenerType},
                            dynamicHandler);

                    listenerCache.put(info, listenerType, listener);
                }

                Method setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                setEventListenerMethod.invoke(view, listener);
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    public static class DynamicHandler implements InvocationHandler {
        // 瀛樻斁浠ｇ悊瀵硅薄锛屾瘮濡侳ragment鎴杤iew holder
        private WeakReference<Object> handlerRef;
        // 瀛樻斁浠ｇ悊鏂规硶
        private final HashMap<String, Method> methodMap = new HashMap<String, Method>(1);

        private static long lastClickTime = 0;

        public DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference<Object>(handler);
        }

        public void addMethod(String name, Method method) {
            methodMap.put(name, method);
        }

        public Object getHandler() {
            return handlerRef.get();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object handler = handlerRef.get();
            if (handler != null) {

                String eventMethod = method.getName();
                if ("toString".equals(eventMethod)) {
                    return DynamicHandler.class.getSimpleName();
                }

                method = methodMap.get(eventMethod);
                if (method == null && methodMap.size() == 1) {
                    for (Map.Entry<String, Method> entry : methodMap.entrySet()) {
                        if (TextUtils.isEmpty(entry.getKey())) {
                            method = entry.getValue();
                        }
                        break;
                    }
                }

                if (method != null) {

                    if (AVOID_QUICK_EVENT_SET.contains(eventMethod)) {
                        long timeSpan = System.currentTimeMillis() - lastClickTime;
                        if (timeSpan < QUICK_EVENT_TIME_SPAN) {
                            LogUtil.d("onClick cancelled: " + timeSpan);
                            return null;
                        }
                        lastClickTime = System.currentTimeMillis();
                    }

                    try {
                        return method.invoke(handler, args);
                    } catch (Throwable ex) {
                        throw new RuntimeException("invoke method error:" +
                                handler.getClass().getName() + "#" + method.getName(), ex);
                    }
                } else {
                    LogUtil.w("method not impl: " + eventMethod + "(" + handler.getClass().getSimpleName() + ")");
                }
            }
            return null;
        }
    }
}
