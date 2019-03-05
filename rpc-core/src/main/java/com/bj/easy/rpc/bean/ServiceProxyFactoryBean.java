package com.bj.easy.rpc.bean;

import com.bj.easy.rpc.loadbalance.LoadBalance;
import com.bj.easy.rpc.message.*;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author xiejunquan
 * @create 2019/2/13 17:33
 */
public class ServiceProxyFactoryBean implements FactoryBean<Object> {

    private final static Logger logger = LoggerFactory.getLogger(ServiceProxyFactoryBean.class);

    private String appId;
    private Map<String, SocketChannel> channels;
    private LoadBalance loadBalance;
    private Class iface;

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { iface },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if (Object.class.getName().equals(method.getDeclaringClass().getName())) {
                            logger.error("proxy class-method not support [{}.{}]", method.getDeclaringClass().getName(), method.getName());
                            throw new RuntimeException("proxy class-method not support");
                        }

                        RpcRequest request = new RpcRequest();
                        request.setCreateMillisTime(System.currentTimeMillis());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        Class<?> returnClass = method.getReturnType();

                        SocketChannel socketChannel = getLoadbalanceChannel();
                        System.out.println(socketChannel);
                        if (socketChannel == null) {
                            throw new Exception("socketChannel is null, channels:" + channels);
                        }
                        if(returnClass == void.class){
                            Message message = new Message(MessageID.get(), MessageType.ASYNC_REQUEST.id, request);
                            Executor.asyncRequest(socketChannel, message);
                            return null;
                        }
                        Message message = new Message(MessageID.get(), MessageType.SYNC_REQUEST.id, request);
                        RpcResponse response = Executor.syncRequest(socketChannel, message);

                        // valid response
                        if (response == null) {
                            throw new Exception("Network request fail, response not found.");
                        }
                        if (response.isError()) {
                            throw new RuntimeException(response.getError());
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }

    @Override
    public Class<?> getObjectType() {
        return iface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public SocketChannel getLoadbalanceChannel(){
        if(channels == null || channels.isEmpty()){
            return null;
        }
        int index = loadBalance.getIndex("");
        int begin = 0;
        for(SocketChannel socketChannel : channels.values()){
            if(begin == index){
                return socketChannel;
            }
            begin++;
        }
        return null;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setChannels(Map<String, SocketChannel> channels) {
        this.channels = channels;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }
}
