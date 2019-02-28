package com.bj.easy.rpc.manager;

import io.netty.channel.socket.SocketChannel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiejunquan
 * @create 2019/1/17 11:50
 */
public class NettyChannelManager {

    private static ConcurrentHashMap<String, SocketChannel> map = new ConcurrentHashMap<>();

    private NettyChannelManager(){

    }

    public static ConcurrentHashMap<String, SocketChannel> get(){
        return map;
    }

    public static void add(String address, SocketChannel socketChannel){
        SocketChannel old = map.get(address);
        if(old != null && old == socketChannel){
            return;
        }
        map.put(address, socketChannel);
    }

    public static void add(String host, int port, SocketChannel socketChannel) {
        String clientId = getClientId(host, port);
        add(clientId, socketChannel);
    }

    public static SocketChannel get(String host, int port) {
        String clientId = getClientId(host, port);
        return map.get(clientId);
    }

    public static SocketChannel get(String address){
        return map.get(address);
    }

    public static void remove(String host, int port) {
        remove(getClientId(host, port));
    }

    public static void remove(String address){
        SocketChannel socketChannel = map.remove(address);
        if(socketChannel != null){
            socketChannel.close();
        }
    }

    public static void remove(SocketChannel socketChannel){
        socketChannel.close();
        map.remove(socketChannel);
    }

    public static void removeAll(){
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            SocketChannel socketChannel = map.get(key);
            socketChannel.close();
            it.remove();
        }
    }

    public static void destroy(){
        removeAll();
    }

    private static String getClientId(String host, int port){
        return host + ":" + port;
    }
}
