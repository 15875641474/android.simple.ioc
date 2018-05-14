package com.hzc.manage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huang zong cheng on 2017/12/14.
 * 328854225@qq.com
 */

public class HzcInjectManage {

    private static boolean init = false;
    private static List<String> injectPros = new ArrayList<>();
    private static Map<Integer, Object> injectCaches = new HashMap<>();

    private HzcInjectManage() {
    }

    public static void inject(Object context) {
        int size = injectPros.size();
        for (int i = 0; i < size; i++) {
            String name = "com.hzc.inject." + injectPros.get(i) + ".model.Inject$" + context.getClass().getName().replace(".", "$");
            try {
                Object obj = Thread.currentThread().getContextClassLoader().loadClass(name).getConstructors()[0].newInstance(context);
                injectCaches.put(context.hashCode(), obj);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unInject(Object context) {
        try {
            Object obj = injectCaches.get(context.hashCode());
            Method[] methods = obj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("destroy")) {
                    method.invoke(obj, context);
                    break;
                }
            }
            injectCaches.remove(context.hashCode());
        } catch (Exception e) {

        }
    }

    public static synchronized void unInit() {
        injectPros.clear();
        injectCaches.clear();
    }

    public static synchronized void init(String[] projects) {
        if (init)
            return;
        init = true;
        for (String pro : projects) {
            try {
                Class cls = Class.forName(String.format("com.hzc.inject.%s.HzcInject", pro));
                Object obj = cls.getMethod("infoService").invoke(null, new Object[]{});
                if (obj != null && obj instanceof Map && ((Map) obj).size() > 0) {
                    injectPros.add(pro);
                }
            } catch (Exception e) {

            }
        }
    }
}
