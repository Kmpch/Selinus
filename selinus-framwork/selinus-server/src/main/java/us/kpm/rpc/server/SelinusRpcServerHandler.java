package us.kpm.rpc.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kpm.rpc.common.SelinusRpcRequest;
import us.kpm.rpc.common.SelinusRpcResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * Description: SelinusRpcServerHandler
 * PackageName: us.kpm.rpc.server
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SelinusRpcServerHandler.java
 * Create at:  2019/2/25
 * Copyright:   Copyright (c)2019
 */
public class SelinusRpcServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelinusRpcServerHandler.class);


    private Map<String,Object> serviceBeanMap;

    public SelinusRpcServerHandler(Map<String, Object> serviceBeanMap) {
        this.serviceBeanMap = serviceBeanMap;
    }

    public SelinusRpcServerHandler() {

    }

    /**
     * 重写父类读取通道方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        LOGGER.info("SelinusRpcServerHandler.channelRead reciveMsg:{}",JSON.toJSONString(msg));
        SelinusRpcRequest request = (SelinusRpcRequest) msg;
        SelinusRpcResponse response = handle(request);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private SelinusRpcResponse handle(SelinusRpcRequest request){

        SelinusRpcResponse response = new SelinusRpcResponse();
        response.setRequestId(UUID.randomUUID().toString());
        response.setResponseId(request.getRequestId());

        try {
            //获取类名(接口名称)
            String className = request.getClassName();
            //获取到方法名
            String methodName = request.getMethodName();
            //获取到参数类型
            Class<?>[] parameterTypes = request.getParameterTypes();
            //获取到参数列表
            Object[] parameters = request.getParameters();
            //根据类名（接口名）获取class
            Class<?> clz = Class.forName(className);
            //获取对象
            Object serviceBean = serviceBeanMap.get(className);
            if(serviceBean == null){
                throw new RuntimeException("未找到相应的对象,serviceBean:" + JSON.toJSON(serviceBean)
                        +"，className:" + className +
                        ",serviceBeanMap:" + JSON.toJSONString(serviceBeanMap));
            }
            //获取对应的方法对象
            Method method = clz.getMethod(methodName, parameterTypes);
            if(method == null){
                throw new RuntimeException("未找到相应的方法,methodName:" + methodName +
                        ",parameterTypes:" + JSON.toJSONString(parameters));
            }

            //反射调用
            Object result = method.invoke(serviceBean, parameters);
            response.setSuccess(true);
            response.setResult(result);
        }catch (Exception e) {
            LOGGER.error("SelinusRpcServerHandler.handle request error,e:{}",e);
           response.setSuccess(false);
           response.setThrowable(e);
        }
        return response;

    }


}
