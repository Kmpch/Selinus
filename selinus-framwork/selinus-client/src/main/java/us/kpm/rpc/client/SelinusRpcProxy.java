package us.kpm.rpc.client;

import us.kpm.rpc.common.SelinusRpcRequest;
import us.kpm.rpc.common.SelinusRpcResponse;
import us.kpm.rpc.register.SelinusRpcDiscover;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Description: SelinusRpcProxy
 * PackageName: us.kpm.rpc.client
 *
 * @author: chenglulu
 * @version: 1.0
 * Filename:    SelinusRpcProxy.java
 * Create at:  2019/2/26
 * Copyright:   Copyright (c)2019
 */
public class SelinusRpcProxy  {


    private SelinusRpcDiscover discover;

    public <T> T getInstance(Class<T> interfaceClass){

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader()
                , new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


                        //获取到被调用的类名
                        String className = method.getDeclaringClass().getName();
                        //获取到方法的参数列表
                        Class<?>[] parameterTypes = method.getParameterTypes();
                         return new SelinusRpcClient(
                                convertRequest(method, args, className, parameterTypes),
                                discover).send();
                    }
                });
    }


    /**
     * 转换成Request
     * @param
     * @return
     */
    private SelinusRpcRequest convertRequest(Method method, Object[] args,
                                             String className, Class<?>[] parameterTypes) {
        //构建请求对象
        SelinusRpcRequest request = new SelinusRpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(parameterTypes);
        return request;
    }


}
