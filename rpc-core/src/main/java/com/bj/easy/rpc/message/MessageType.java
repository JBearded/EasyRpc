package com.bj.easy.rpc.message;

import com.bj.easy.rpc.handler.*;
import io.netty.channel.Channel;

/**
 * @author xiejunquan
 * @create 2019/1/17 12:53
 */
public enum MessageType {
    NONE((byte)-1, "none", new MessageHandler() {
        @Override
        public void messageReceived(Channel channel, Message message) {

        }
    }),
    PING((byte)1, "ping", new PingMessageHandler()),
    PONG((byte)2, "pong", new PongMessageHandler()),
    SYNC_REQUEST((byte)3, "syncRequest", new SyncRequestMessageHandler()),
    ASYNC_REQUEST((byte)4, "asyncRequest", new AsyncRequestMessageHandler()),
    RESPONSE((byte)5, "response", new ResponseMessageHandler()),
    SUBSCRIBE((byte)6, "subscribe", null),
    PUBLISH((byte)7, "publish", null);
    public final byte id;
    public final String desc;
    public final MessageHandler handler;

    MessageType(byte id, String desc, MessageHandler handler) {
        this.id = id;
        this.desc = desc;
        this.handler = handler;
    }

    public static MessageType get(int id){
        for(MessageType messageType : values()){
            if(messageType.id == id){
                return messageType;
            }
        }
        return NONE;
    }
}
