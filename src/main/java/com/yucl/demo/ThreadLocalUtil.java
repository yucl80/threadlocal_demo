package com.yucl.demo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ThreadLocalUtil {
    protected static Method createInheritedMap;
    protected static Method getEntry;
    protected static Field entryValueField;
    protected static Method setThreadLocalMap;
    protected static Field tableField;
    protected static Field threadLocalsField;
    protected static Field inheritableThreadLocalsFiled;

    static {
        try {
            Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            createInheritedMap = ThreadLocal.class
                    .getDeclaredMethod("createInheritedMap", threadLocalMapClass);
            createInheritedMap.setAccessible(true);
            setThreadLocalMap = threadLocalMapClass.getDeclaredMethod("set", ThreadLocal.class, Object.class);
            setThreadLocalMap.setAccessible(true);
            tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);

            Class<?> entryClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
            getEntry = entryClass.getMethod("get");
            getEntry.setAccessible(true);
            entryValueField = entryClass.getDeclaredField("value");
            entryValueField.setAccessible(true);
            threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            inheritableThreadLocalsFiled = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsFiled.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Iterable<ThreadLocal<?>> copyInheritableThreadLocals(Thread caller) throws Exception {
        List<ThreadLocal<?>> threadLocals = new ArrayList<>();
        Object map = inheritableThreadLocalsFiled.get(caller);
        if (map != null) {
            Object o = createInheritedMap.invoke(null, map);
            inheritableThreadLocalsFiled.set(Thread.currentThread(), o);
            Object tbl = tableField.get(o);
            int length = Array.getLength(tbl);
            for (int i = 0; i < length; i++) {
                Object entry = Array.get(tbl, i);
                if (entry != null) {
                    Object value = getEntry.invoke(entry);
                    threadLocals.add((ThreadLocal<?>) value);
                }
            }
        }
        return threadLocals;
    }

    public static Iterable<ThreadLocal<?>> copyThreadLocals(Thread caller) throws Exception {
        List<ThreadLocal<?>> threadLocals = new ArrayList<>();
        Object map = threadLocalsField.get(caller);
        if (map != null) {
            ThreadLocal threadLocal = new ThreadLocal();
            threadLocal.set("__init__");
            threadLocal.remove();
            Object map1 = threadLocalsField.get(Thread.currentThread());

            Object tbl = tableField.get(map);
            int length = Array.getLength(tbl);
            for (int i = 0; i < length; i++) {
                Object entry = Array.get(tbl, i);
                if (entry != null) {
                    Object key = getEntry.invoke(entry);
                    Object entryValue = entryValueField.get(entry);
                    setThreadLocalMap.invoke(map1, (ThreadLocal<?>) key, entryValue);
                    threadLocals.add((ThreadLocal<?>) key);
                }
            }

        }
        return threadLocals;
    }

    public static String dump(Thread thread) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Object threadLocals = threadLocalsField.get(thread);
            Object inheritableThreadLocals = inheritableThreadLocalsFiled.get(thread);
            Object threadLocalsTable = tableField.get(threadLocals);
            Object inheritableThreadLocalsTable = tableField.get(inheritableThreadLocals);
            stringBuilder.append("threadLocals:{").append(String.join(",", dumpThreadLocals(threadLocalsTable))).append("},");
            stringBuilder.append("inheritableThreadLocals:{").append(String.join(",", dumpThreadLocals(inheritableThreadLocalsTable))).append("}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private static List<String> dumpThreadLocals(Object tbl) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        int length = Array.getLength(tbl);
        List<String> valueList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Object entry = Array.get(tbl, i);
            Object value = null;
            String valueClass = null;
            if (entry != null) {
                value = entryValueField.get(entry);
                if (value != null) {
                    valueClass = value.getClass().getName();
                }
                valueList.add(new StringBuilder().append("\"").append(valueClass).append("\"").append(":")
                        .append("\"").append(value).append("\"").toString());
            }
        }
        return valueList;
    }

}
