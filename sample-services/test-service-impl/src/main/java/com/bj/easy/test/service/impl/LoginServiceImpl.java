package com.bj.easy.test.service.impl;

import com.bj.easy.test.service.api.LoginService;
import org.springframework.stereotype.Service;

/**
 * @author xiejunquan
 * @create 2019/2/27 13:06
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public boolean login(String name, String pwd) {
        System.out.println(name + "login ++++++++++++++++++++++++++++++++");
        return false;
    }
}
