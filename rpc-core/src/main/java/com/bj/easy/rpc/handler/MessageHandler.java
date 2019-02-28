package com.bj.easy.rpc.handler;

import com.bj.easy.rpc.message.Message;
import io.netty.channel.Channel;

/**
 * @author xiejunquan
 * @create 2019/1/17 12:54
 */
public interface MessageHandler {

    void messageReceived(Channel channel, Message message);
}
