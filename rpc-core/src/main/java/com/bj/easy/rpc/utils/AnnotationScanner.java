package com.bj.easy.rpc.utils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author xiejunquan
 * @create 2019/2/18 10:47
 */
public class AnnotationScanner {

    public static Map<String, List<Class<?>>> scan(String packages, Class<? extends Annotation> annotationClass){
        if(packages == null){
            return Collections.emptyMap();
        }
        Map<String, List<Class<?>>> result = new HashMap<>();
        String[] packageArray = packages.split(";");
        for(String pack : packageArray){
            List<Class<?>> clazzList = new ArrayList<>();
            Set<Class<?>> clazzSet = PackageScanner.getClasses(pack);
            for(Class<?> clazz : clazzSet){
                Annotation matchAnnotation = clazz.getAnnotation(annotationClass);
                if(matchAnnotation != null){
                    clazzList.add(clazz);
                }
            }
            result.put(pack, clazzList);
        }
        return result;
    }
}
