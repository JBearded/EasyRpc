package com.bj.easy.rpc.handler;

import com.bj.easy.rpc.manager.NettyChannelManager;
import com.bj.easy.rpc.message.Executor;
import com.bj.easy.rpc.message.Message;
import com.bj.easy.rpc.message.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

/**
 * @author xiejunquan
 * @create 2019/1/17 13:01
 */
public class PingMessageHandler implements MessageHandler {
    @Override
    public void messageReceived(Channel channel, Message message) {
        String identity = (String) message.getBody();
        NettyChannelManager.add(identity, (SocketChannel) channel);
        Message<String> respMessage = new Message(message.getId(), MessageType.PONG.id, "ok");
        Executor.asyncRequest(channel, respMessage);
    }
}
