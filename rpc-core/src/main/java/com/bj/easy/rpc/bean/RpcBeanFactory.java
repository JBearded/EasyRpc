package com.bj.easy.rpc.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiejunquan
 * @create 2019/2/13 16:21
 */
public class RpcBeanFactory {

    private final static Map<String, Object> beans = new HashMap<>();

    public static Object get(Class clazz){
        return beans.get(clazz.getName());
    }

    public static Object get(String key){
        return beans.get(key);
    }

    public static void put(Object object){
        beans.put(object.getClass().getInterfaces()[0].getName(), object);
    }
}
