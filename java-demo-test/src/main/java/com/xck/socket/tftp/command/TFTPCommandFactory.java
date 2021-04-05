package com.xck.socket.tftp.command;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * @data 2021-04-04 21:00:00
 * @author xck
 */
public class TFTPCommandFactory {

    public static TFTPCommand create(String[] args) throws Exception{
        TFTPCommand command = null;
        if ("put".equals(args[0])) {
            command = new TFTPWrite();
            command.assemble(args);
        } else if ("get".equals(args[1])) {

        } else {
            throw new UnsupportedOperationException("不支持操作" + args[0]);
        }
        return command;
    }

    public static DatagramPacket createRequest(InetAddress address, int port, byte opCode, String fileName, String mode) {

        int byteLen = 2 + fileName.length() + 1 + mode.length() + 1;
        ByteBuffer buff = ByteBuffer.allocate(byteLen);

        //2字节表示操作码,
        buff.put((byte) 0);
        buff.put(opCode);
        //操作的文件名
        buff.put(fileName.getBytes());
        buff.put(TFTPCommand.zeroByte);
        //操作模式
        buff.put(mode.getBytes());
        buff.put(TFTPCommand.zeroByte);

        DatagramPacket packet = new DatagramPacket(buff.array(), buff.limit(), address, port);

        return packet;
    }

    public static DatagramPacket createData(InetAddress address, int port, byte[] data, byte seq) throws Exception {

        int byteLen = 2 + 2 + data.length;
        ByteBuffer buff = ByteBuffer.allocate(byteLen);

        //2字节表示操作码,
        buff.put((byte) 0);
        buff.put((byte) TFTPCommand.TFTPOcode.DATA.getValue());
        buff.put((byte) 0);
        buff.put(seq);
        //操作的文件名
        buff.put(data);

        DatagramPacket packet = new DatagramPacket(buff.array(), buff.limit(), address, port);

        return packet;
    }

    public static DatagramPacket createAck(InetAddress address, int port, byte opCode, byte blockNum) throws Exception {

        int byteLen = 2 + 2;
        ByteBuffer buff = ByteBuffer.allocate(byteLen);

        //2字节表示操作码,
        buff.put((byte) 0);
        buff.put(opCode);
        buff.put((byte) 0);
        buff.put(blockNum);

        DatagramPacket packet = new DatagramPacket(buff.array(), buff.limit(), address, port);

        return packet;
    }
}
