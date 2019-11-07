package com.yucl.demo;

import java.util.HashMap;
import java.util.Map;

public class InheritableThreadLocalMap {
    private final static InheritableThreadLocal<Map<Object,Object>> inheritableThreadLocalMap = new InheritableThreadLocal<>();

    public static void put(Object key,Object value){
        Map<Object,Object> map = inheritableThreadLocalMap.get();
        if(map == null){
            map = new HashMap<>();
            inheritableThreadLocalMap.set(map);
        }
        map.put(key,value);
    }

    public static Object get(Object key){
        Map<Object,Object> map = inheritableThreadLocalMap.get();
        if(map != null){
            return map.get(key);
        }else {
            return null;
        }
    }

    public static void remvoe(Object key){
        Map<Object,Object> map = inheritableThreadLocalMap.get();
        if(map != null){
            map.remove(key);
        }
    }

    public static void remove(){
        inheritableThreadLocalMap.remove();
    }
}
