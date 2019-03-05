package com.bj.easy.server;

import com.bj.easy.rpc.bean.spring.RpcServiceServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiejunquan
 * @create 2019/2/18 17:11
 */
public class App {

    private final static Logger logger = LoggerFactory.getLogger(App.class);

    private final static ReentrantLock LOCK = new ReentrantLock();
    private final static Condition STOP = LOCK.newCondition();

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        context.start();
        Thread.sleep(1000 * 60);
        RpcServiceServer serviceServer = (RpcServiceServer) context.getBean("rpcServiceServer2");
        serviceServer.destroy();

        Runtime.getRuntime().addShutdownHook(new Thread(
                new ShutdownHookRunnable(context),
                "StartMain-shutdown-hook"));
        try{
            LOCK.lock();
            STOP.await();
        }catch (Exception e){
            logger.warn("service stopped, interrupted by other thread!", e);
        }finally {
            LOCK.unlock();
        }
    }

    static class ShutdownHookRunnable implements Runnable{

        private ClassPathXmlApplicationContext context;

        public ShutdownHookRunnable(ClassPathXmlApplicationContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            try{
                context.stop();
            }catch (Exception e){
                logger.warn("StartMain stop exception", e);
            }
            logger.info("jvm exit, all service stopped.");
            try {
                LOCK.lock();
                STOP.signalAll();
            } finally {
                LOCK.unlock();
            }
        }
    }
}
