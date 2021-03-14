package com.xck;

import com.xck.wechat.Command;
import com.xck.wechat.LoginRequestPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 解码，编码器
 */
public class PacketCodeC {

    private static final int MAGIC_NUMBER = 0x12345678;

    /**
     * 编码：将数据包转换为二进制
     *
     * @param packet
     * @return
     */
    public ByteBuf encode(Packet packet) {
        //返回适配io读写的相关内存，它会尽可能创建一个直接内存，这样写的效率更高。
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        //序列化对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        //编码
        byteBuf.writeInt(MAGIC_NUMBER); //魔数
        byteBuf.writeByte(packet.getVersion()); //版本号
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm()); // 序列化算法
        byteBuf.writeByte(packet.getCommand()); //命令
        byteBuf.writeInt(bytes.length); //数据长度
        byteBuf.writeBytes(bytes); //数据

        return byteBuf;
    }

    /**
     * 解码：将二进制数据包解析为命令对象
     *
     * @return
     */
    public Packet decode(ByteBuf byteBuf) {
        // 跳过魔数
        byteBuf.skipBytes(4);
        //跳过版本号
        byteBuf.skipBytes(1);
        //序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();
        //指令
        byte command = byteBuf.readByte();
        //数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null){
            return serializer.deserialize(requestType, bytes);
        }

        System.err.println("无法解析, command=" + command
                + ", serializeAlgorithm=" + serializeAlgorithm);

        return null;
    }

    public Class<? extends Packet> getRequestType(byte command) {
        switch (command) {
            case Command.LOGIN_REQUEST:
                return LoginRequestPacket.class;
        }

        return null;
    }

    public Serializer getSerializer(byte serializeAlgorithm) {
        switch (serializeAlgorithm) {
            case SerializerAlgorithm.JSON: return Serializer.DEFAULT;
        }

        return null;
    }
}
