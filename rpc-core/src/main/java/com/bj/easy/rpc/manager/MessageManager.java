package com.bj.easy.rpc.manager;

import com.bj.easy.rpc.message.Message;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiejunquan
 * @create 2019/1/19 18:09
 */
public class MessageManager {
    private final static ConcurrentHashMap<Long, Message> manager = new ConcurrentHashMap<>();

    private MessageManager(){

    }

    public static void put(Message message){
        manager.put(message.getId(), message);
    }

    public static Message get(Long id){
        //每次从消息缓冲中获取消息数据的时候, 清除前10000到前11000的1000个消息
        //这1000个消息是timeout时未取出的数据, 为了防止占用内存, 所以要清除
        //而前10000的消息是预设并发有10000, 需要保留前10000的数据
        long removeEndIndex = id - 100000;
        long removeBeginIndex = removeEndIndex - 10000;
        for(long i = removeBeginIndex; i < removeEndIndex; i++){
            manager.remove(Long.valueOf(i));
        }
        return manager.remove(id);
    }

    public static void destroy(){
        manager.clear();
    }

}
