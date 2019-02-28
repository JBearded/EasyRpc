package com.bj.easy.rpc.bean.spring;

import com.bj.easy.rpc.bean.RpcBeanFactory;
import com.bj.easy.rpc.bean.annotation.RpcService;
import com.bj.easy.rpc.server.NettyServer;
import com.bj.easy.rpc.utils.AnnotationScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author xiejunquan
 * @create 2019/2/13 17:49
 */
public class RpcServiceServer implements ApplicationListener<ContextRefreshedEvent> {

    private int port;
    private String servicePackage;

    private NettyServer nettyServer;

    public RpcServiceServer(int port, String servicePackage) {
        this.port = port;
        this.servicePackage = servicePackage;
        this.nettyServer = new NettyServer(port);
        this.nettyServer.bind();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            Map<String, List<Class<?>>> services = AnnotationScanner.scan(servicePackage, Service.class);
            ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
            for(Map.Entry<String, List<Class<?>>> entry : services.entrySet()){
                List<Class<?>> clazzList = entry.getValue();
                for(Class<?> clazz : clazzList){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if(interfaces != null){
                        boolean rpc = false;
                        for(Class<?> ifac : interfaces){
                            if(ifac.getAnnotation(RpcService.class) != null){
                                rpc = true;
                                break;
                            }
                        }
                        if(rpc){
                            Object object = applicationContext.getBean(clazz);
                            RpcBeanFactory.put(object);
                        }
                    }
                }
            }
        }
    }

    public void destroy(){
        try{
            nettyServer.destroy();
        }catch (Exception e){

        }
    }

}
