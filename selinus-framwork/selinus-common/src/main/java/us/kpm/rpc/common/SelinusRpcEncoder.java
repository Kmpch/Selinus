package us.kpm.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Data;
import us.kpm.rpc.utils.SerializerUtils;

/**
 * Description: SelinusRpcEncoder
 * PackageName: us.kpm.rpc.common
 *
 * @author: luffych  编码  序列化字节码传递
 * @version: 1.0
 * Filename:    SelinusRpcEncoder.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 */
public class SelinusRpcEncoder extends MessageToByteEncoder {

    /**
     * 对象类型
     */
    private Class genericClz;

    public SelinusRpcEncoder(){

    }

    public SelinusRpcEncoder(Class clz){
        this.genericClz = clz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {

        if(genericClz.isInstance(o)){
            byte[] bytes = SerializerUtils.serialize(o);
            out.writeBytes(bytes);
        }
    }
}
