package com.xck.redis;

import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * @Classname ObjectConverter
 * @Description TODO
 * @Date 2021/1/10 10:34
 * @Created by xck503c
 */
public class ObjectConverter extends BaseCodec {



    @Override
    public Decoder<Object> getValueDecoder() {
        return null;
    }

    @Override
    public Encoder getValueEncoder() {
        return null;
    }
}
