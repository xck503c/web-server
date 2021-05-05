package com.xck.socket;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NIOServer {

    public static void main(String[] args) throws Exception{

        Selector selector = Selector.open();

        ServerSocketChannel listenerChannel = ServerSocketChannel.open();
        listenerChannel.socket().bind(new InetSocketAddress(8000));
        listenerChannel.configureBlocking(false);
        listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

    }
}
