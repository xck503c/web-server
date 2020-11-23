package com.xck.socket;

/**
 * @Classname Main
 * @Description TODO
 * @Date 2020/11/22 22:05
 * @Created by xck503c
 */
public class Main {

    public static void main(String[] args) {
        SocketRWTimeOut.Client client = new SocketRWTimeOut.Client();
        client.start();
    }
}