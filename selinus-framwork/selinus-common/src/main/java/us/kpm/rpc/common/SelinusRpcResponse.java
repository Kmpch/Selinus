package us.kpm.rpc.common;

import lombok.Data;

/**
 * Description: SelinusRpcResponse
 * PackageName: us.kpm.rpc.common
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SelinusRpcResponse.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 */
@Data
public class SelinusRpcResponse {


    /**
     * 回执消息的信息Id
     */
    private String responseId;

    /**
     * 请求消息的消息Id
     */
    private String requestId;

    /**
     * 响应的消息是否成功
     */
    private boolean success;
    /**
     * 响应的数据结果
     */
    private Object result;

    /**
     * 响应的数据结果
     */
    private Throwable throwable;

}
