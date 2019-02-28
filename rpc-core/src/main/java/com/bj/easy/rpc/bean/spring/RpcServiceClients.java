package com.bj.easy.rpc.bean.spring;

import com.bj.easy.rpc.client.NettyClient;
import com.bj.easy.rpc.manager.NettyChannelManager;
import com.bj.easy.rpc.message.Executor;
import com.bj.easy.rpc.message.Message;
import com.bj.easy.rpc.message.MessageID;
import com.bj.easy.rpc.message.MessageType;
import com.bj.easy.rpc.utils.IpUtil;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiejunquan
 * @create 2019/2/13 17:49
 */
public class RpcServiceClients implements BeanDefinitionRegistryPostProcessor {

    private String appId;
    private Map<String, String> servicePackage;
    private Map<String, NettyClient> clients = new HashMap<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public RpcServiceClients(String appId, Map<String, String> servicePackage) {
        this.appId = appId;
        this.servicePackage = servicePackage;
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(servicePackage.size());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for(Map.Entry<String, String> entry : servicePackage.entrySet()){
            String server = entry.getKey();
            String[] hostPort = server.split(":");
            NettyClient client = new NettyClient(hostPort[0], Integer.valueOf(hostPort[1]));
            client.start();
            clients.put(server, client);
            new Thread(new PingRunner(server, appId)).start();
            String pack = entry.getValue();
            RpcServiceClientScanner scanner = new RpcServiceClientScanner(beanDefinitionRegistry);
            scanner.setServers(server);
            scanner.scan(StringUtils.tokenizeToStringArray(pack, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        for(String server : clients.keySet()){
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(
                    new PingRunner(server, appId),
                    0,
                    5000L,
                    TimeUnit.MILLISECONDS);
        }
    }

    public void destroy(){
        scheduledThreadPoolExecutor.shutdownNow();
    }

    class PingRunner implements Runnable {

        private Logger logger = LoggerFactory.getLogger(PingRunner.class);

        private String server;
        private String appId;

        public PingRunner(String server, String appId) {
            this.server = server;
            this.appId = appId;
        }

        @Override
        public void run() {
            try{
                String ip = IpUtil.getIp();
                Message message = new Message(MessageID.get(), MessageType.PING.id, ip + ":" + appId);
                Executor.syncRequest(NettyChannelManager.get(server), message);
            }catch (Exception e){
                logger.warn("failed to ping server:" + server + " appId:" + appId, e);
            }
        }
    }


}
