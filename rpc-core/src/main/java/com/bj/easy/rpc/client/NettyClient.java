package com.bj.easy.rpc.client;

import com.bj.easy.rpc.message.MessageDecoder;
import com.bj.easy.rpc.message.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author xiejunquan
 * @create 2019/1/17 10:15
 */
public class NettyClient {

    private final String appId;
    private final String host;
    private final int port;
    private NettyChannelActiveListener activeListener;
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private NettyClientConnectionListener connectionListener;
    private SocketChannel socketChannel;

    public NettyClient(String appId, String host, int port, NettyChannelActiveListener activeListener) {
        this.appId = appId;
        this.host = host;
        this.port = port;
        this.activeListener = activeListener;
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.connectionListener = new NettyClientConnectionListener(this);
    }

    public NettyClient newBootstrap(){
        this.bootstrap = new Bootstrap();
        return this;
    }

    public void start() {
        final NettyClientMessageHandler messageHandler = new NettyClientMessageHandler(this);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host, port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new MessageEncoder());
                socketChannel.pipeline().addLast(new MessageDecoder());
                socketChannel.pipeline().addLast(messageHandler);
            }
        });
        bootstrap.connect(host, port).addListener(connectionListener);
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public String getAppId() {
        return appId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public NettyChannelActiveListener getActiveListener() {
        return activeListener;
    }

    public void destroy(){
        eventLoopGroup.shutdownGracefully();
        socketChannel.close().awaitUninterruptibly();
    }

    @Override
    public String toString() {
        return "{" +
                "host:'" + host + '\'' +
                ", port:" + port +
                '}';
    }
}
