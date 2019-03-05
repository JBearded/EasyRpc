package com.bj.easy.rpc.handler;

import com.bj.easy.rpc.manager.MessageManager;
import com.bj.easy.rpc.manager.SyncManager;
import com.bj.easy.rpc.message.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiejunquan
 * @create 2019/1/17 13:01
 */
public class PongMessageHandler implements MessageHandler{

    private final static Logger logger = LoggerFactory.getLogger(PongMessageHandler.class);
    @Override
    public void messageReceived(Channel channel, Message message) {
        MessageManager.put(message);
        SyncManager.notifyAll(message.getId());
    }
}
