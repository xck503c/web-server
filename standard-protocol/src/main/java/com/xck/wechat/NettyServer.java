package com.xck.wechat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {

    public static void main(String[] args) {
        //两大线程组
        //监听端口，获取连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理数据读写
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        //服务端启动引导类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //给引导类配置两大线程组,引导类的线程模型也就定型了
        serverBootstrap.group(bossGroup, workGroup)
                //指定我们服务端的 IO 模型为NIO，这里也是对NIO连接对抽象
                .channel(NioServerSocketChannel.class)
                //定义后续每条连接的数据读写，业务处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //NioSocketChannel是netty对NIO类型的连接抽象
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FirstServerHandler());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_TIMEOUT, 15000);

        //异步方法，调用之后立即返回
        //我们可以给返回值ChannelFuture添加监听器
        serverBootstrap.bind(8888).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("端口绑定成功!");
                }else {
                    System.out.println("端口绑定失败!");
                }
            }
        });
    }
}
