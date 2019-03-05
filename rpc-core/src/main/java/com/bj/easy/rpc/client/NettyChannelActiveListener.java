package com.bj.easy.rpc.client;

import io.netty.channel.socket.SocketChannel;

/**
 * @author xiejunquan
 * @create 2019/3/5 16:02
 */
public interface NettyChannelActiveListener {

    void active(String ip, int port, SocketChannel socketChannel);

    void inactive(String ip, int port);
}
