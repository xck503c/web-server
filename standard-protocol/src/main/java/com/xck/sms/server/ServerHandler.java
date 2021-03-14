package com.xck.sms.server;

import com.xck.sms.Handler;
import com.xck.sms.cmpp.CmppActiveTestMessage;
import com.xck.sms.cmpp.CmppMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends Handler {

    private int port;

    public ServerHandler(int port) {
        this.port = port;
    }

    //当接收到数据的时候会调用
    //ChannelHandlerContext提供了各种操作用于触发IO
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        System.out.println(ctx.channel() + " server read");
        ByteBuf in = (ByteBuf)msg;
        try {
            CmppMessage cmppMessage = CmppMessage.createMessage(in, this);
//            cmppMessage.setHandler(this);
            cmppMessage.doSomething(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道注册，端口：" + port + ", channel: " + ctx.channel());
    }

    //当连接被建立
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道建立，端口：" + port + ", channel: " + ctx.channel());
    }

    //处理事件的时候，出现io错误，通常情况在这里需要关闭连接并打印日志，但是你也可以自己定义，比如说
    //关闭连接前发送错误码
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            System.out.println("idle event close, channel: " + ctx.channel());
            CmppActiveTestMessage activeTestMessage = new CmppActiveTestMessage(this);
            activeTestMessage.doSomething(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接被关闭, channel: " + ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("处理器被移除, channel: " + ctx.channel());
        try {
            ServerConnectRegistry.cmppunlogin(cmppUserBean.getUserId());
            cmppUserBean = null;
        } catch (Exception e) {
            System.out.println("处理器未关联，正常退出");
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接取消注册, channel: " + ctx.channel());
    }
}
