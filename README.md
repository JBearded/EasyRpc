# EasyRpc

### 服务端
* 编写接口, 使用RpcService作为注解   
>    
    @RpcService
    public interface LoginService {
        boolean login(String name, String pwd);
    }
    
* 编写接口实现, 使用spring的Service注解
>   
    @Service
    public class LoginServiceImpl implements LoginService {
        @Override
        public boolean login(String name, String pwd) {
            System.out.println(name + "login ++++++++++++++++++++++++++++++++");
            return false;
        }
    }

* 配置spring配置
>    
    <context:component-scan base-package="com.bj.easy.test.service"/>

    <bean id="rpcServiceServer" class="com.bj.easy.rpc.bean.spring.RpcServiceServer">
        <constructor-arg name="port" value="9999"></constructor-arg>
        <constructor-arg name="servicePackage" value="com.bj.easy.test.service"></constructor-arg>
    </bean>
    
* 启动服务
>    
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
    context.start();

### 客户端
* 导入接口模块(文件)
* 配置spring配置
>
    <context:component-scan base-package="com.bj.easy.test.service"/>

    <bean id="rpcServiceClients" class="com.bj.easy.rpc.bean.spring.RpcServiceClients" destroy-method="destroy">
        <constructor-arg name="appId" value="921106"></constructor-arg>
        <constructor-arg name="servicePackage">
            <map>
                <entry key="127.0.0.1:9999" value="com.bj.easy.test.service.api"></entry>
            </map>
        </constructor-arg>
    </bean>
* 启动客户端
> 
    ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
    LoginService loginService = context.getBean(LoginService.class);
    System.out.println(loginService.login("大胡子", ""));