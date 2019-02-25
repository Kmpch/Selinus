package us.kpm.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import us.kpm.rpc.utils.SerializerUtils;

import java.util.List;

/**
 * Description: SelinusRpcDecoder
 * PackageName: us.kpm.rpc.common
 *
 * @author: chenglulu
 * @version: 1.0
 * Filename:    SelinusRpcDecoder.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 * Company:     songxiaocai
 * Modification History:
 * Date              Author      Version     Description
 * ------------------------------------------------------------------
 * 2019/2/24     chenglulu    1.0         1.0 Version
 */
public class SelinusRpcDecoder extends ByteToMessageDecoder {


    /**
     * 对象类型
     */
    private Class genericClz;

    public SelinusRpcDecoder (){

    }

    public SelinusRpcDecoder(Class clz){
        this.genericClz = clz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {

        int size = in.readableBytes();
        if(size < 4){
            return;
        }
        byte []  bytes = new byte[size];
        in.readBytes(bytes);
        Object o = SerializerUtils.deserialize(bytes, genericClz);
        list.add(o);
        //刷新缓存
        channelHandlerContext.flush();
    }
}
