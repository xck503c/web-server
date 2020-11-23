package com.xck.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Classname SocketRWTimeOut
 * @Description
 * @Date 2020/11/22 20:44
 * @Created by xck503c
 */
public class SocketRWTimeOut {

    public static void main(String[] args) {
        noSetServiceRWTimeout();
    }

    public static void noSetServiceRWTimeout(){
        Server server = new Server();
        server.start();

        Client client = new Client();
        client.start();
    }

    private static class Server extends Thread{

        @Override
        public void run(){
            startServer();
        }

        public void startServer(){
            ServerSocket server = null;
            Socket socket = null;
            try {
                server = new ServerSocket(8888);
                System.out.println("listen");
                socket = server.accept();
                socket.setSoTimeout(5000);
                System.out.println("server connect success");
                InputStream is = socket.getInputStream();
                int i = -1;
                byte[] b = new byte[20];
                while ((i = is.read(b, 0, b.length)) != -1) {
                    String s = new String(b, "utf-8");
                    System.out.println(s);
                    Thread.sleep(6000);
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("close server");
        }
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
//                InetAddress addr = InetAddress.getByName( "127.0." );
                socket.connect(new InetSocketAddress(8888));
                socket.setSoTimeout(3000);
                OutputStream out = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                System.out.println("client connect success");
                int i = -1;
                while ((i = is.read()) != -1) {
                    System.out.println(i);
//                    byte[] b = ("client sent --- hello *** " + (i++)).getBytes("UTF-8");
//                    System.out.println("print len=" + b.length + " content="+ "client sent --- hello *** " + (i++));
//                    out.write(b);
//                    out.flush();
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