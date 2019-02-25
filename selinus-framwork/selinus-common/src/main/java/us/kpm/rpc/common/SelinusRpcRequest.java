package us.kpm.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Description: SelinusRpcRequest
 * PackageName: us.kpm.rpc.common
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SelinusRpcRequest.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 */
@Data
public class SelinusRpcRequest {

    /**
     * 请求消息的消息Id
     */
    private String requestId;

    /**
     * 请求的具体的类名(接口名称)
     */
    private String className;

    /**
     * 请求的具体的方法名称
     */
    private String methodName;

    /**
     * 请求的方法参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求的方法参数列表
     */
    private Object[] parameters;

}
