package com.bj.easy.rpc.bean;

import com.bj.easy.rpc.message.RpcRequest;
import com.bj.easy.rpc.message.RpcResponse;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

/**
 * @author xiejunquan
 * @create 2019/2/13 16:15
 */
public class BeanServiceProxy {

    public static RpcResponse invoke(RpcRequest request){
        RpcResponse response = new RpcResponse();
        try {
            Object serviceBean = RpcBeanFactory.get(request.getClassName());
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
            Object result = serviceFastMethod.invoke(serviceBean, parameters);
            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t.getMessage());
        }
        return response;
    }
}
