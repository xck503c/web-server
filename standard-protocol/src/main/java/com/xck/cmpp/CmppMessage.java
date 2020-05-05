package com.xck.cmpp;

import com.xck.Handler;
import com.xck.server.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class CmppMessage {

    public static final int CONNECT = 0x00000001;
    public static final int CONNECT_RESP = 0x80000001;
    public static final int TEST = 0x00000008;
    public static final int TEST_RESP = 0x80000008;
    public static final int SUBMIT = 0x00000004;
    public static final int SUBMIT_RESP = 0x80000004;

    protected CmppHeader cmppHeader;
    protected Handler handler;

    protected int flag = REC; //0-send 1-rec
    public static final int SEND = 0;
    public static final int REC = 1;

    public static CmppMessage createMessage(ByteBuf byteBuf) throws Exception{
        CmppHeader cmppHeader = new CmppHeader(byteBuf);
        switch (cmppHeader.getCommandId()){
            case CmppMessage.CONNECT: return new CmppConnectMessage(null, cmppHeader, byteBuf);
            case CmppMessage.CONNECT_RESP: return new CmppConnectRespMessage(null, cmppHeader, byteBuf);
            case CmppMessage.TEST: return new CmppActiveTestMessage(null, cmppHeader, byteBuf);
            case CmppMessage.TEST_RESP: return new CmppActiveTestRespMessage(null, cmppHeader, byteBuf);
            default: return null;
        }
    }

    public CmppMessage(Handler handler){
        this.handler = handler;
    }

    public abstract void doSomething(ChannelHandlerContext ctx) throws Exception;

    public abstract ByteBuf getMessageBuf();

    public final CmppHeader getCmppHeader() {
        return cmppHeader;
    }

    public final Handler getHandler() {
        return handler;
    }

    public final void setHandler(ServerHandler handler) {
        this.handler = handler;
    }

    public final int getSeq() throws Exception{
        CmppUserBean cmppUserBean = handler.getCmppUserBean();
        if(cmppUserBean == null){
            throw new Exception("getSeq, no login");
        }
        try{
            cmppUserBean.seqLock();
            return cmppUserBean.getSeq();
        }finally {
            cmppUserBean.seqUnLock();
        }
    }
}
