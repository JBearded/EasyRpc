package com.bj.easy.rpc.message;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xiejunquan
 * @create 2019/1/21 10:59
 */
public class MessageID {
    private final static AtomicLong atomicLong = new AtomicLong(0);

    public static Long get(){
        return atomicLong.incrementAndGet();
    }
}
