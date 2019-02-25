package us.kpm.rpc.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.collect.Maps;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;

/**
 * Description: SerializerUtils
 *  基于 Protostuff 实现的序列化工具类
 * PackageName: us.kpm.rpc.utils
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SerializerUtils.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 */
public class SerializerUtils {

    private static Map<Class<?>,Schema<?>> cachedSchema = Maps.newConcurrentMap();

    private  static  Objenesis objenesis = new ObjenesisStd(true);


    /**
     * 获取schema
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> Schema<T> getSchema(Class<T> clz){

        Schema<T> schema = (Schema<T>)cachedSchema.get(clz);
        //不存在，则创建
        if(schema == null){
            schema = RuntimeSchema.createFrom(clz);
            // 创建完成加入缓存
            if(schema != null){
                cachedSchema.put(clz,schema);
            }
        }
        return schema;
    }

    /**
     * 序列化: 对象 -> 字节数组
     * @param t
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T t){

        Class<T> clz = (Class<T>) t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clz);
            return ProtostuffIOUtil.toByteArray(t,schema,buffer);
        } catch (Exception e) {
           throw new IllegalStateException(e.getMessage(),e);
        }finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化：字节数组 -> 对象
     * @param data
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] data, Class<T> clz){

        /**
         * 实例化
         * 通过ObjenesisStd 解决一个类没有空的参数构造方法时,直接使用getInstance方法抛出的异常
         */try {
            T t = (T)objenesis.newInstance(clz);

            //获取类的schema
            Schema<T> schema = getSchema(clz);

            //组合对象
            ProtostuffIOUtil.mergeFrom(data,t,schema);
            return t;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(),e);
        }

    }





}
