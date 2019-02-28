package com.bj.easy.rpc.handler;

import com.bj.easy.rpc.manager.MessageManager;
import com.bj.easy.rpc.message.Message;
import io.netty.channel.Channel;

/**
 * @author xiejunquan
 * @create 2019/2/13 16:58
 */
public class ResponseMessageHandler implements MessageHandler{
    @Override
    public void messageReceived(Channel channel, Message message) {
        MessageManager.put(message);
    }
}
