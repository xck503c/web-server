package com.xck.cmpp;

import com.xck.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class CmppActiveTestMessage extends CmppMessage{

    public CmppActiveTestMessage(Handler handler, CmppHeader cmppHeader, ByteBuf bodyBuffer) {
        super(handler);
        this.flag = REC;
        this.cmppHeader = cmppHeader;
    }

    public CmppActiveTestMessage(Handler handler) throws Exception{
        super(handler);
        this.flag = SEND;
        this.cmppHeader = new CmppHeader(0, CmppMessage.TEST, getSeq());
    }

    @Override
    public void doSomething(ChannelHandlerContext ctx) throws Exception {
        if (flag == SEND) {
            System.out.println("send test message");
            ctx.writeAndFlush(getMessageBuf());
        }else if(flag == REC){
            System.out.println("receive test message");
            CmppHeader respHeader = new CmppHeader(0, CmppMessage.TEST_RESP, cmppHeader.getSequenceId());
            CmppActiveTestRespMessage respMessage = new CmppActiveTestRespMessage(handler, respHeader);
            respMessage.doSomething(ctx);
        }
    }

    @Override
    public ByteBuf getMessageBuf() {
        return cmppHeader.getHeaderBuf();
    }
}
