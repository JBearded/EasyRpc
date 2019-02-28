package com.bj.easy.rpc.manager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiejunquan
 * @create 2019/1/19 15:41
 */
public class SyncManager {

    private final static ConcurrentHashMap<Long, Object> lockMap = new ConcurrentHashMap<>();

    private SyncManager(){

    }

    public static void await(Long id, long timeout) {
        Object newObject = new Object();
        Object oldObject = lockMap.putIfAbsent(id, newObject);
        Object lock = (oldObject == null) ? newObject : oldObject;
        synchronized (lock){
            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                notifyAll(id);
                System.out.println("await error" +  e);
            }
        }
    }

    public static void notifyAll(Long id){
        Object lock = lockMap.remove(id);
        if(lock != null){
            synchronized (lock){
                lock.notifyAll();
            }
        }
    }

    public static void destroy(){
        lockMap.clear();
    }
}
