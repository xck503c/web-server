package com.xck.redis;

import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * @Classname JdkObjCodec
 * @Description TODO
 * @Date 2021/1/10 10:34
 * @Created by xck503c
 */
public class JdkObjValueStringCodec extends JdkObjCodec {

    public static final JdkObjValueStringCodec INSTANCE = new JdkObjValueStringCodec();

    @Override
    public Decoder<Object> getMapValueDecoder() {
        return StringCodec.INSTANCE.getMapValueDecoder();
    }

    @Override
    public Encoder getMapValueEncoder() {
        return StringCodec.INSTANCE.getMapValueEncoder();
    }
}
