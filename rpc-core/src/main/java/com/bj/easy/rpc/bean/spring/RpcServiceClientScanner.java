package com.bj.easy.rpc.bean.spring;

import com.bj.easy.rpc.bean.ServiceProxyFactoryBean;
import com.bj.easy.rpc.bean.annotation.RpcService;
import com.bj.easy.rpc.loadbalance.LoadBalance;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

/**
 * @author xiejunquan
 * @create 2019/2/13 17:53
 */
public class RpcServiceClientScanner extends ClassPathBeanDefinitionScanner {

    private final static Logger logger = LoggerFactory.getLogger(RpcServiceClientScanner.class);

    private String appId;
    private Map<String, SocketChannel> channels;
    private LoadBalance loadBalance;

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public void setChannels(Map<String, SocketChannel> channels) {
        this.channels = channels;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public RpcServiceClientScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        for (BeanDefinitionHolder holder : beanDefinitions) {
            try{
                ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();
                definition.getPropertyValues().add("appId", appId);
                definition.getPropertyValues().add("loadBalance", loadBalance);
                definition.getPropertyValues().add("channels", channels);
                definition.getPropertyValues().add("iface", definition.getBeanClassName());
                definition.setBeanClass(ServiceProxyFactoryBean.class);
            }catch (Exception e){
                logger.error("error to set rpc service proxy", e);
            }
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }


}
