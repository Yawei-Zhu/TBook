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

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.ViewInjector;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public final class ViewInjectorImpl implements ViewInjector {

    private static final HashSet<Class<?>> IGNORED = new HashSet<Class<?>>();

    static {
        IGNORED.add(Object.class);
        IGNORED.add(Activity.class);
        IGNORED.add(android.app.Fragment.class);
        try {
            IGNORED.add(Class.forName("android.support.v4.app.Fragment"));
            IGNORED.add(Class.forName("android.support.v4.app.FragmentActivity"));
        } catch (Throwable ignored) {
        }
    }

    private static final Object lock = new Object();
    private static volatile ViewInjectorImpl instance;

    private ViewInjectorImpl() {
    }

    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ViewInjectorImpl();
                }
            }
        }
        x.Ext.setViewInjector(instance);
    }

    @Override
    public void inject(View view) {
        injectObject(view, view.getClass(), new ViewFinder(view));
    }

    @Override
    public void inject(Activity activity) {
        //鑾峰彇Activity鐨凜ontentView鐨勬敞瑙�        
    	Class<?> handlerType = activity.getClass();
        try {
            ContentView contentView = findContentView(handlerType);
            if (contentView != null) {
                int viewId = contentView.value();
                if (viewId > 0) {
                    Method setContentViewMethod = handlerType.getMethod("setContentView", int.class);
                    setContentViewMethod.invoke(activity, viewId);
                }
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }

        injectObject(activity, handlerType, new ViewFinder(activity));
    }

    @Override
    public void inject(Object handler, View view) {
        injectObject(handler, handler.getClass(), new ViewFinder(view));
    }

    @Override
    public View inject(Object fragment, LayoutInflater inflater, ViewGroup container) {
        // inject ContentView
        View view = null;
        Class<?> handlerType = fragment.getClass();
        try {
            ContentView contentView = findContentView(handlerType);
            if (contentView != null) {
                int viewId = contentView.value();
                if (viewId > 0) {
                    view = inflater.inflate(viewId, container, false);
                }
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }

        // inject res & event
        injectObject(fragment, handlerType, new ViewFinder(view));

        return view;
    }

    /**
     * 浠庣埗绫昏幏鍙栨敞瑙iew
     */
    private static ContentView findContentView(Class<?> thisCls) {
        if (thisCls == null || IGNORED.contains(thisCls)) {
            return null;
        }
        ContentView contentView = thisCls.getAnnotation(ContentView.class);
        if (contentView == null) {
            return findContentView(thisCls.getSuperclass());
        }
        return contentView;
    }

    @SuppressWarnings("ConstantConditions")
    private static void injectObject(Object handler, Class<?> handlerType, ViewFinder finder) {

        if (handlerType == null || IGNORED.contains(handlerType)) {
            return;
        }

        // 浠庣埗绫诲埌瀛愮被閫掑綊
        injectObject(handler, handlerType.getSuperclass(), finder);

        // inject view
        Field[] fields = handlerType.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {

                Class<?> fieldType = field.getType();
                if (
                /* 涓嶆敞鍏ラ潤鎬佸瓧娈�*/     Modifier.isStatic(field.getModifiers()) ||
                /* 涓嶆敞鍏inal瀛楁 */    Modifier.isFinal(field.getModifiers()) ||
                /* 涓嶆敞鍏ュ熀鏈被鍨嬪瓧娈�*/  fieldType.isPrimitive() ||
                /* 涓嶆敞鍏ユ暟缁勭被鍨嬪瓧娈�*/  fieldType.isArray()) {
                    continue;
                }

                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    try {
                        View view = finder.findViewById(viewInject.value(), viewInject.parentId());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        } else {
                            throw new RuntimeException("Invalid @ViewInject for "
                                    + handlerType.getSimpleName() + "." + field.getName());
                        }
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        } // end inject view

        // inject event
        Method[] methods = handlerType.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {

                if (Modifier.isStatic(method.getModifiers())
                        || !Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }

                //妫�煡褰撳墠鏂规硶鏄惁鏄痚vent娉ㄨВ鐨勬柟娉�                
                Event event = method.getAnnotation(Event.class);
                if (event != null) {
                    try {
                        // id鍙傛暟
                        int[] values = event.value();
                        int[] parentIds = event.parentId();
                        int parentIdsLen = parentIds == null ? 0 : parentIds.length;
                        //寰幆鎵�湁id锛岀敓鎴怴iewInfo骞舵坊鍔犱唬鐞嗗弽灏�                        
                        for (int i = 0; i < values.length; i++) {
                            int value = values[i];
                            if (value > 0) {
                                ViewInfo info = new ViewInfo();
                                info.value = value;
                                info.parentId = parentIdsLen > i ? parentIds[i] : 0;
                                method.setAccessible(true);
                                EventListenerManager.addEventMethod(finder, info, event, handler, method);
                            }
                        }
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        } // end inject event

    }

}
