package com.xck.socket.tftp.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @data 2021-04-04 21:00:00
 * @author xck
 */
public class TFTPWrite extends TFTPCommand{

    private File file = null;
    private DatagramPacket packet = null;

    @Override
    public void assemble(String[] args) throws UnknownHostException {
        super.assemble(args);

        file = new File(args[3]);


        packet = TFTPCommandFactory.createRequest(ip, port
                , (byte) TFTPOcode.WRITE.getValue(), file.getName()
                , TCTPMode.BINARY.getValue());

    }

    @Override
    public void execute() throws SocketException, IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);

        new ReceiveTask(socket, ip, port).start();
    }

    public byte[] readFile() throws FileNotFoundException, IOException {
        RandomAccessFile accessFile = new RandomAccessFile(file, "r");
        ByteBuffer buff = ByteBuffer.allocate(512);
        int len = accessFile.read(buff.array());
        if(len < 512){
            ByteBuffer tmp = ByteBuffer.allocate(len);
            buff.get(tmp.array());
            return tmp.array();
        }
        return buff.array();
    }

    public class ReceiveTask extends Thread {
        private DatagramSocket socket;
        private InetAddress address;
        private int port;
        private byte seq = -1;

        public ReceiveTask(DatagramSocket socket, InetAddress address, int port) {
            this.socket = socket;
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            try {

                while (true) {
                    byte[] buff = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buff, buff.length
                            , address, port);
                    socket.receive(packet);

                    ByteBuffer byteBuffer = ByteBuffer.wrap(buff);
                    byteBuffer.get();
                    switch (byteBuffer.get()) {
                        case 4 :
                            byteBuffer.get();
                            byte blockNum = byteBuffer.get();
                            if(seq == blockNum){
                                System.out.println("重复确认");
                                return;
                            }
                            byte[] data = readFile();
                            DatagramPacket dataPackage = TFTPCommandFactory.createData(
                                    address, packet.getPort(), data, ++blockNum);
                            socket.send(dataPackage);
                            if (data.length < 512){
                                return;
                            }
                            seq = blockNum;

                        case 5:
                            byte[] codeByte = new byte[2];
                            byteBuffer.get(codeByte);
                            String code = new String(codeByte);
                            byteBuffer.mark();
                            int count = 0;
                            while (byteBuffer.get() != 0){
                                ++count;
                                continue;
                            }
                            byteBuffer.reset();

                            byte[] errMsgByte = new byte[count];
                            byteBuffer.get(errMsgByte);

                            System.out.println(new String(errMsgByte));
                            return;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
