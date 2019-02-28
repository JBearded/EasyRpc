package com.bj.easy.rpc.server;

import com.bj.easy.rpc.handler.NettyMessageHandler;
import com.bj.easy.rpc.message.MessageDecoder;
import com.bj.easy.rpc.message.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author xiejunquan
 * @create 2019/1/17 11:38
 */
public class NettyServer {

    private int port;
    private NioServerSocketChannel serverSocketChannel;
    private Thread thread;

    public NettyServer(int port) {
        this.port = port;
    }

    public void bind() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    EventLoopGroup boss = new NioEventLoopGroup();
                    EventLoopGroup worker = new NioEventLoopGroup();
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                            .option(ChannelOption.TCP_NODELAY, true);
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new NettyMessageHandler());
                        }
                    });
                    ChannelFuture channelFuture = bootstrap.bind(port).sync();
                    if (channelFuture.isSuccess()){
                        serverSocketChannel = (NioServerSocketChannel) channelFuture.channel();
                        System.out.println("server start ......");
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void destroy(){
        serverSocketChannel.close();
    }

}
