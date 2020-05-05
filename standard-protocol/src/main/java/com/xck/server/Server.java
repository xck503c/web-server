package com.xck.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Server {

    private String portStr;
    private ServerBootstrap serverBootstrap;

    public Server(String portStr) {
        this.portStr = portStr;
    }

    public void run() throws Exception{

        //NioEventLoopGroup is a multithreaded event loop that handles I/O operation.
        //The first one, often called 'boss', accepts an incoming connection.
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        //处理连接的通信，当boss接收连接后，就会将这个连接注册到worker上
        //使用多少线程，以及如果将线程映射到创建的channel上，取决于EventLoopGroup的实现
        //我们可以通过构造函数来配置
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //
            serverBootstrap = new ServerBootstrap(); // (2)
            serverBootstrap.group(bossGroup, workerGroup)
                    //在这里我们使用NioServerSocketChannel来接收连接
                    .channel(NioServerSocketChannel.class) // (3)
                    //当接收到新的channel的时候会调用，我们可以对channel进行一些配置，如，你可以
                    //为ChannelPipeline添加一些处理器来实现应用
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(60, 60, 60, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ServerFrameDecoder(Integer.MAX_VALUE, 0, 4));
                            ch.pipeline().addLast(new ServerHandler(ch.localAddress().getPort()));
                            System.out.println("init server handler, port:"
                                    + ch.localAddress().getPort());
                        }
                    })
                    //设置一些socket参数
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);// (6)
            bindPort();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private void bindPort() throws Exception{
        // Bind and start to accept incoming connections.
        String[] portArr = portStr.split(",");
        ChannelFuture f = null; // (7)
        for (String portStr : portArr) {
            f = serverBootstrap.bind(Integer.parseInt(portStr)).sync();
        }

        // Wait until the server socket is closed.
        // In this example, this does not happen, but you can do that to gracefully
        // shut down your server.
        f.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws Exception{
        new Server("8888,8889").run();
    }
}
