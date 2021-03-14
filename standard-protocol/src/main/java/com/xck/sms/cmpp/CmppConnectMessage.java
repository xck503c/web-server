package com.xck.sms.cmpp;

import com.xck.sms.Handler;
import com.xck.sms.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class CmppConnectMessage extends CmppMessage{
    private String sourceAddr;
    private String authenticatorSource;
    private byte version = 32;
    private int timeStamp;
    private String requestIp;

    //用于构造请求连接的消息
    public CmppConnectMessage(Handler handler, CmppHeader cmppHeader, String sourceAddr, String pwd, int timeStamp) throws Exception{
        super(handler);
        this.cmppHeader = cmppHeader;
        this.sourceAddr = sourceAddr;
        ByteBuf md5Buf = Unpooled.buffer(16);
        md5Buf.writeCharSequence(sourceAddr, Charset.forName("GBK"));
        md5Buf.writeBytes(new byte[9]);
        md5Buf.writeCharSequence(pwd, Charset.forName("GBK"));
        md5Buf.writeInt(timeStamp);
        this.authenticatorSource = StringUtils.md5ConvertByte(new String(md5Buf.array()), "ISO8859_1");
        this.timeStamp = timeStamp;
    }

    //用于解析请求连接的消息
    public CmppConnectMessage(Handler handler, CmppHeader cmppHeader, ByteBuf bodyBuf) throws Exception{
        super(handler);
        this.cmppHeader = cmppHeader;
        this.sourceAddr = bodyBuf.readCharSequence(6, Charset.forName("GBK")).toString().trim();
        this.authenticatorSource = bodyBuf.readCharSequence(16, Charset.forName("ISO8859_1")).toString().trim();
        this.version = bodyBuf.readByte();
        this.timeStamp = bodyBuf.readInt();
    }

    @Override
    public void doSomething(ChannelHandlerContext ctx) throws Exception{
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        requestIp = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        System.out.println("user_id:" + this.sourceAddr + ", loginMode:" + ", timeStamp:" + this.timeStamp + ", Version:" + this.version + ", from ip:" + requestIp + ", port:" + port);

        CmppConnectRespMessage respMessage = new CmppConnectRespMessage(handler, this);
        respMessage.doSomething(ctx);
    }

    @Override
    public ByteBuf getMessageBuf(){
        ByteBuf byteBuf = Unpooled.buffer(CmppHeader.HEADER_LEN+CmppHeader.CONNRCT_BODY_LEN);
        byteBuf.writeBytes(cmppHeader.getHeaderBuf());
        byteBuf.writeCharSequence(sourceAddr, Charset.forName("GBK"));
        byteBuf.writeCharSequence(authenticatorSource, Charset.forName("ISO8859_1"));
        byteBuf.writeByte(version);
        byteBuf.writeInt(timeStamp);
        return byteBuf;
    }

    public String getAuthenticatorSource() {
        return authenticatorSource;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "CmppConnectMessage{" +
                "sourceAddr='" + sourceAddr + '\'' +
                ", authenticatorSource='" + authenticatorSource + '\'' +
                ", version=" + version +
                ", timeStamp=" + timeStamp +
                ", cmppHeader=" + cmppHeader +
                '}';
    }
}
