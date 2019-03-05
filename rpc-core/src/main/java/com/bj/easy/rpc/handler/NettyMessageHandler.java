package com.bj.easy.rpc.handler;

import com.bj.easy.rpc.message.Message;
import com.bj.easy.rpc.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiejunquan
 * @create 2019/1/21 15:53
 */
public class NettyMessageHandler extends SimpleChannelInboundHandler<Message> {

    private final static Logger logger = LoggerFactory.getLogger(NettyMessageHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message msg) throws Exception {
        byte type = msg.getType();
        MessageType messageType = MessageType.get(type);
        MessageHandler handler = messageType.handler;
        handler.messageReceived(ctx.channel(), msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
