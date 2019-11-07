package com.yucl.demo;


import java.util.HashMap;
import java.util.Map;

public class ThreadLocalMap {
    private final static ThreadLocal<Map<Object, Object>> threadLocalMap = new ThreadLocal<>();

    public static void put(Object key, Object value) {
        Map<Object, Object> map = threadLocalMap.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocalMap.set(map);
        }
        map.put(key, value);
    }

    public static Object get(Object key) {
        Map<Object, Object> map = threadLocalMap.get();
        if (map != null) {
            return map.get(key);
        } else {
            return null;
        }
    }

    public static void remvoe(Object key) {
        Map<Object, Object> map = threadLocalMap.get();
        if (map != null) {
            map.remove(key);
        }
    }

    public static void remove(){
        threadLocalMap.remove();
    }
}
