package com.xck.redis;

import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedisPipeline extends Pipeline {

    public static Pipeline pipelined(Jedis jedis) {
        try {
            Pipeline pipeline = new RedisPipeline();
            pipeline.setClient(jedis.getClient());

            Field f = getField(jedis.getClass(), "pipeline");
            if(f != null){
                f.setAccessible(true);
                f.set(jedis, pipeline);
                return pipeline;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return jedis.pipelined();
    }

    public static Field getField(Class<?> clzz, String fieldName){
        List<Field> fieldList = new ArrayList<Field>() ;
        while (clzz != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(clzz .getDeclaredFields()));
            clzz = clzz.getSuperclass(); //得到父类,然后赋给自己
        }
        for (Field f : fieldList) {
            if (f.getName().equals(fieldName)){
                return f;
            }
        }
        return null;
    }

    public Response<byte[]> getrangeInByte(byte[] key, long startOffset, long endOffset) {
        this.getClient(key).getrange(key, startOffset, endOffset);
        return this.getResponse(BuilderFactory.BYTE_ARRAY);
    }
}
