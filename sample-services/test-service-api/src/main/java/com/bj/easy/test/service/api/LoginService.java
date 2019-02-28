package com.bj.easy.test.service.api;

import com.bj.easy.rpc.bean.annotation.RpcService;

/**
 * @author xiejunquan
 * @create 2019/2/27 13:04
 */
@RpcService
public interface LoginService {

    boolean login(String name, String pwd);

}
