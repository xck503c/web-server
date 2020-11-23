package com.xck.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Classname SocketConnectTimeOut
 * @Description
 * @Date 2020/11/23 09:07
 * @Created by xck503c
 */
public class SocketConnectTimeOut {

    public static void main(String[] args) {
        noSetServiceRWTimeout();
    }

    public static void noSetServiceRWTimeout(){

        Client client = new Client();
        client.start();
    }

    public static class Client extends Thread{

        @Override
        public void run(){
            startClient();
        }

        public void startClient(){
            Socket socket = null;
            try {
                socket = new Socket();
                InetAddress addr = InetAddress.getByName("49.235.32.249");
                socket.connect(new InetSocketAddress(addr, 8888), 4000);
                socket.setSoTimeout(3000);
                OutputStream out = socket.getOutputStream();
                System.out.println("client connect success");
                int i = 0;
                while (true) {
                    byte[] b = ("client sent --- hello *** " + (i++)).getBytes("UTF-8");
                    System.out.println("print len=" + b.length + " content="+ "client sent --- hello *** " + (i++));
                    out.write(b);
                    out.flush();
                    Thread.sleep(1000);
                }
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("close client");
        }
    }
}