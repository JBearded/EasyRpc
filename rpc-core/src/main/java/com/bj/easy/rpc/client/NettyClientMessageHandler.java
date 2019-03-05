package com.bj.easy.rpc.client;

import com.bj.easy.rpc.handler.NettyMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xiejunquan
 * @create 2019/1/21 21:34
 */
public class NettyClientMessageHandler extends NettyMessageHandler {

    private final static Logger logger = LoggerFactory.getLogger(NettyClientMessageHandler.class);

    private NettyClient nettyClient;

    public NettyClientMessageHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                nettyClient.newBootstrap().start();
                logger.info("reconnect to " + nettyClient.getHost() + ":" + nettyClient.getPort() + "............");
            }
        }, 30L, TimeUnit.SECONDS);
        nettyClient.getActiveListener().inactive(nettyClient.getHost(), nettyClient.getPort());
        super.channelInactive(ctx);
    }
}
