package com.xck.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.*;

/**
 * @Classname JdkObjCodec
 * @Description TODO
 * @Date 2021/1/10 10:34
 * @Created by xck503c
 */
public class JdkObjCodec extends BaseCodec {

    public static final JdkObjCodec INSTANCE = new JdkObjCodec();

    private final Encoder encoder = new Encoder() {
        @Override
        public ByteBuf encode(Object in) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(in);
            return Unpooled.wrappedBuffer(baos.toByteArray());
        }
    };

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            try {
                byte[] result = new byte[buf.readableBytes()];
                buf.readBytes(result);
                //不能直接buf.array()，操作不支持
                ByteArrayInputStream bais = new ByteArrayInputStream(result);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder<Object> getMapKeyDecoder() {
        return StringCodec.INSTANCE.getMapKeyDecoder();
    }

    @Override
    public Encoder getMapKeyEncoder() {
        return StringCodec.INSTANCE.getMapKeyEncoder();
    }
}
