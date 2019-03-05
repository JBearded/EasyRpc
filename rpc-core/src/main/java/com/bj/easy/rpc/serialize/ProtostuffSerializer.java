package com.bj.easy.rpc.serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiejunquan
 * @create 2019/2/13 14:20
 */
public class ProtostuffSerializer {

    private final static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static <T> Schema<T> getSchema(Class<T> clz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clz);
            cachedSchema.put(clz, schema);
        }
        return schema;
    }

    public static <T> byte[] serialize(T obj){
        Class<T> clz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clz);
        return ProtostuffIOUtil.toByteArray(obj, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public static <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        Objenesis objenesis = new ObjenesisStd();
        T obj = objenesis.newInstance(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, obj, getSchema(clazz));
        return obj;
    }
}
