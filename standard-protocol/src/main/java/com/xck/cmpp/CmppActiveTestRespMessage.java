package com.xck.cmpp;

import com.xck.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;



public class CmppActiveTestRespMessage extends CmppMessage{

    public CmppActiveTestRespMessage(Handler handler, CmppHeader cmppHeader, ByteBuf bodyBuffer) {
        super(handler);
        this.flag = REC;
        this.cmppHeader = cmppHeader;
    }

    public CmppActiveTestRespMessage(Handler handler, CmppHeader cmppHeader) {
        super(handler);
        this.flag = SEND;
        this.cmppHeader = cmppHeader;
    }

    @Override
    public void doSomething(ChannelHandlerContext ctx) throws Exception {
        if (flag == REC) {
            System.out.println("receive test resp msg");
        }else if(flag == SEND){
            System.out.println("send test resp msg");
            ctx.writeAndFlush(getMessageBuf());
        }
    }

    @Override
    public ByteBuf getMessageBuf() {
        return cmppHeader.getHeaderBuf();
    }
}
