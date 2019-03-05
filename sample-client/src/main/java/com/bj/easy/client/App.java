package com.bj.easy.client;

import com.bj.easy.test.service.api.LoginService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author xiejunquan
 * @create 2019/2/18 17:11
 */
public class App {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        LoginService loginService = context.getBean(LoginService.class);
        for(int i = 0; i < 10; i++){
            try{
                System.out.println(loginService.login("大胡子", ""));
                Thread.sleep(1000 * 10);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
