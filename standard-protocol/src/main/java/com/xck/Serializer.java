package com.xck;

public interface Serializer {

    /**
     * 默认序列化类型和序列号算法
     */
    byte JSON_SERIALIZER = 1;

    Serializer DEFAULT = new JSONSerializer();

    /**
     * 序列化算法类型
     * @return
     */
    byte getSerializerAlgorithm();

    /**
     * Java对象转换为二进制
     * @param object
     * @return
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换为Java对象
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
