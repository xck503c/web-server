package com.xck.client;

import com.xck.Handler;
import com.xck.cmpp.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;


public class ClientHandler extends Handler{
    private int seq = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(ctx.channel() + " client read");
        ByteBuf in = (ByteBuf)msg;
        try {
            CmppMessage cmppMessage = CmppMessage.createMessage(this, in);
            cmppMessage.doSomething(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        CmppHeader header = new CmppHeader(CmppHeader.CONNRCT_BODY_LEN, CmppMessage.CONNECT, seq);
        CmppConnectMessage connectMessage = new CmppConnectMessage(this, header, new String("xck001".getBytes(), "GBK")
                , new String("xck123".getBytes(), "GBK"), timeStamp);
        if(++seq < 0){
            seq = 0;
        }
        setCmppUserBean(ClientConnectRegistry.cmpplogin("xck001", ""));
        ctx.writeAndFlush(connectMessage.getMessageBuf());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            System.out.println("idle event, channel: " + ctx.channel());
            CmppActiveTestMessage activeTestMessage = new CmppActiveTestMessage(this);
            activeTestMessage.doSomething(ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("移除, channel: " + ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("取消注册, channel: " + ctx.channel());
    }
}
