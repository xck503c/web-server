package com.xck.wechat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private static int MAX_RETRY = 5; //失败重连重试次数

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        //指定线程模型
        bootstrap.group(workerGroup)
                //设置io模型
                .channel(NioSocketChannel.class)
                //io处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FirstClientHandler());
                    }
                })
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true);

        connect(bootstrap, "127.0.0.1", 8888, MAX_RETRY);
    }

    private static void connect(final Bootstrap bootstrap, final String ip, final int port, final int retry) {
        bootstrap.connect(ip, port)
                .addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("连接成功");
                        } else {
                            System.out.println("连接失败");
                            //失败重连
                            //通常情况下，连接建立失败不会立即重新连接，而是会通过一个指数退避的方式
                            //  比如每隔 1 秒、2 秒、4 秒、8 秒，以 2 的幂次来建立连接
//                            connect(bootstrap, ip, port);
                            int order  = (MAX_RETRY - retry) + 1; //每次+1
                            int delay = 1 << order; //计算间隔
                            //BootstrapConfig是对配置参数对抽象
                            //group返回的是一开始配置的线程模型workGroup
                            System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                            bootstrap.config().group().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    connect(bootstrap, ip, port, retry - 1);
                                }
                            }, delay, TimeUnit.SECONDS);
                        }
                    }
                });
    }
}
