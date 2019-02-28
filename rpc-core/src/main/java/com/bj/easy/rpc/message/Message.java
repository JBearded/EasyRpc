package com.bj.easy.rpc.message;

import java.io.Serializable;

/**
 * @author xiejunquan
 * @create 2019/1/17 14:45
 */
public class Message<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    //同步请求
    //异步请求
    //响应
    //PING
    //PONG
    private byte type;
    private T body;



    public Message(long id, byte type, T body) {
        this.id = id;
        this.type = type;
        this.body = body;
    }

    public Message() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public T getBody() {
        return body;
    }

    public Message<T> setBody(T body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", type:" + type +
                ", body:" + body +
                '}';
    }
}
