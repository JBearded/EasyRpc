package com.bj.easy.rpc.handler;

import com.bj.easy.rpc.bean.BeanServiceProxy;
import com.bj.easy.rpc.message.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiejunquan
 * @create 2019/1/17 13:01
 */
public class AsyncRequestMessageHandler implements MessageHandler{

    private final static Logger logger = LoggerFactory.getLogger(AsyncRequestMessageHandler.class);

    @Override
    public void messageReceived(Channel channel, Message message) {
        RpcRequest request = (RpcRequest) message.getBody();
        BeanServiceProxy.invoke(request);
    }
}
