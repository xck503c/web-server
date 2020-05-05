package com.xck.cmpp;

import com.xck.Handler;
import com.xck.server.ServerConnectRegistry;
import com.xck.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class CmppConnectRespMessage extends CmppMessage{
    private byte status;
    private String authenticatorSource;
    private byte version = 32;
    private String userId;
    private String requestIp;
    private int timestamp;

    public CmppConnectRespMessage(Handler handler, CmppConnectMessage connectMessage) throws Exception{
        super(handler);
        this.flag = SEND;
        this.authenticatorSource = connectMessage.getAuthenticatorSource();
        this.userId = connectMessage.getSourceAddr();
        this.requestIp = connectMessage.getRequestIp();
        this.timestamp = connectMessage.getTimeStamp();
        this.cmppHeader = new CmppHeader(CmppHeader.CONNRCT_RESP_BODY_LEN, CmppMessage.CONNECT_RESP, connectMessage.getCmppHeader().getSequenceId());
    }

    public CmppConnectRespMessage(Handler handler, CmppHeader cmppHeader, ByteBuf bodyBuf) throws Exception{
        super(handler);
        this.flag = REC;
        this.cmppHeader = cmppHeader;
        this.status = bodyBuf.readByte();
        this.authenticatorSource = bodyBuf.readCharSequence(16, Charset.forName("ISO8859_1")).toString();
        this.version = bodyBuf.readByte();
    }

    @Override
    public void doSomething(ChannelHandlerContext ctx) throws Exception{
        if (flag == SEND) {
            System.out.println("resp connect");
            this.status = CmppUserBean.valid(handler, authenticatorSource, userId, requestIp, timestamp);
            ctx.writeAndFlush(getMessageBuf());
            if(status != 0){
                System.out.println(CmppUserBean.validLog(status));
                ctx.close(); //登陆失败需要关闭连接
            }
        }else if(flag == REC){
            System.out.println("receive connect resp");
        }
    }

    @Override
    public ByteBuf getMessageBuf() {
        ByteBuf byteBuf = Unpooled.buffer(CmppHeader.HEADER_LEN+CmppHeader.CONNRCT_RESP_BODY_LEN);
        byteBuf.writeBytes(cmppHeader.getHeaderBuf());
        byteBuf.writeByte(status);
        String md5 = StringUtils.md5Convert(status + authenticatorSource + "xck123");
        byteBuf.writeCharSequence(md5, Charset.forName("GBK"));
        byteBuf.writeByte(version);
        return byteBuf;
    }

    @Override
    public String toString() {
        return "CmppConnectRespMessage{" +
                "status=" + status +
                ", authenticatorSource='" + authenticatorSource + '\'' +
                ", version=" + version +
                ", cmppHeader=" + cmppHeader +
                '}';
    }
}
