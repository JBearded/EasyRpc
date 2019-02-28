package com.bj.easy.rpc.client;

import com.bj.easy.rpc.manager.NettyChannelManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xiejunquan
 * @create 2019/1/21 21:48
 */
public class NettyClientConnectionListener implements ChannelFutureListener {

    private final static Logger logger = LoggerFactory.getLogger(NettyClientConnectionListener.class);

    private NettyClient nettyClient;

    public NettyClientConnectionListener(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if(!future.isSuccess()){
            EventLoop eventLoop = future.channel().eventLoop();
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    nettyClient.newBootstrap().start();
                    logger.info("reconnect to " + nettyClient.getHost() + ":" + nettyClient.getPort() + "............");
                }
            }, 30L, TimeUnit.SECONDS);
        }else{
            SocketChannel socketChannel = (SocketChannel) future.channel();
            NettyChannelManager.add(nettyClient.getHost(), nettyClient.getPort(), socketChannel);
            nettyClient.setSocketChannel(socketChannel);
            logger.info("client start " + nettyClient.getHost() + ":" + nettyClient.getPort() + "............");
        }
    }
}
