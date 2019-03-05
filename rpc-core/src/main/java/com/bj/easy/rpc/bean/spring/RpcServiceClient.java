package com.bj.easy.rpc.bean.spring;

import com.bj.easy.rpc.client.NettyChannelActiveListener;
import com.bj.easy.rpc.client.NettyClient;
import com.bj.easy.rpc.loadbalance.RRLoadBalance;
import com.bj.easy.rpc.message.Executor;
import com.bj.easy.rpc.message.Message;
import com.bj.easy.rpc.message.MessageID;
import com.bj.easy.rpc.message.MessageType;
import com.bj.easy.rpc.utils.IpUtil;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiejunquan
 * @create 2019/2/13 17:49
 */
public class RpcServiceClient implements BeanDefinitionRegistryPostProcessor {

    private final String appId;
    private final String servers;
    private final String packages;
    private final RRLoadBalance loadBalance;
    private final Map<String, NettyClient> clients;
    private final Map<String, SocketChannel> channels;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public RpcServiceClient(String appId, String servers, String packages) {
        this.appId = appId;
        this.servers = servers;
        this.packages = packages;
        this.clients = new HashMap<>();
        this.channels = new ConcurrentHashMap<>();
        this.loadBalance = new RRLoadBalance(1);
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        String[] serverArray = servers.split(";");
        for(String server : serverArray){
            String[] hostPort = server.split(":");
            NettyClient client = new NettyClient(appId, hostPort[0], Integer.valueOf(hostPort[1]), new NettyChannelActiveListener() {
                @Override
                public void active(String ip, int port, SocketChannel socketChannel) {
                    channels.put(ip + ":" + port, socketChannel);
                    loadBalance.reload(channels.size());
                }

                @Override
                public void inactive(String ip, int port) {
                    channels.remove(ip + ":" + port);
                    loadBalance.reload(channels.size());
                }
            });
            client.start();
            clients.put(server, client);
        }
        RpcServiceClientScanner scanner = new RpcServiceClientScanner(beanDefinitionRegistry);
        scanner.setAppId(appId);
        scanner.setChannels(channels);
        scanner.setLoadBalance(loadBalance);
        scanner.scan(StringUtils.tokenizeToStringArray(packages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(
                new PingRunner(appId),
                0,
                30000L,
                TimeUnit.MILLISECONDS);
    }

    public void destroy(){
        scheduledThreadPoolExecutor.shutdownNow();
    }

    class PingRunner implements Runnable {

        private Logger logger = LoggerFactory.getLogger(PingRunner.class);
        private String appId;

        public PingRunner(String appId) {
            this.appId = appId;
        }

        @Override
        public void run() {
            for(NettyClient client : clients.values()){
                try{
                    String ip = IpUtil.getIp();
                    Message message = new Message(MessageID.get(), MessageType.PING.id, ip + ":" + appId);
                    SocketChannel socketChannel = client.getSocketChannel();
                    Executor.syncRequest(socketChannel, message);
                }catch (Exception e){
                    logger.warn("failed to ping host:" + client.getHost() + " port:" + client.getPort() + " appId:" + appId, e);
                }
            }

        }
    }


}
