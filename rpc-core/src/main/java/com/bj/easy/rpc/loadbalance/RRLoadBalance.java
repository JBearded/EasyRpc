package com.bj.easy.rpc.loadbalance;

/**
 * 轮询调度
 *
 * @author 谢俊权
 * @create 2016/5/6 14:26
 */
public class RRLoadBalance implements LoadBalance{

    private volatile int lastServerIndex = 0;

    private volatile int serverSize;

    public RRLoadBalance(int serverSize) {
        this.serverSize = serverSize;
    }

    public void reload(int serverSize){
        this.lastServerIndex = 0;
        this.serverSize = serverSize;
    }

    @Override
    public int getIndex(String key) {
        int count = this.lastServerIndex;
        count = (count + 1) % this.serverSize;
        this.lastServerIndex = count;
        return this.lastServerIndex;
    }
}
