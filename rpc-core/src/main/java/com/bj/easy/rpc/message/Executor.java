package com.bj.easy.rpc.message;

import com.bj.easy.rpc.manager.MessageManager;
import com.bj.easy.rpc.manager.SyncManager;
import io.netty.channel.Channel;

/**
 * @author xiejunquan
 * @create 2019/1/21 11:05
 */
public class Executor {

    public static <T> T syncRequest(Channel channel, Message message){
        Long id = message.getId();
        channel.writeAndFlush(message);
        SyncManager.await(id, 5000);
        Message<RpcResponse> resMessage = MessageManager.get(id);
        return (T) resMessage.getBody();
    }

    public static void asyncRequest(Channel channel, Message message){
        channel.writeAndFlush(message);
    }
}
