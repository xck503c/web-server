package com.xck.cmpp;

import com.xck.Handler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class CmppSubmitMessage extends CmppMessage{
    private long msgId;
    private byte pkTotal; //相同msgId的总条数，从1开始
    private byte pkNumber; //相同msgId的序号，从1开始
    private byte registeredDelivery; //是否要返回状态报告，0-不需要，1-需要，2-产生SMC话单
    private byte msgLevel; //信息的级别
    private String serviceId; //业务类型
    private byte feeUserType; //计费用户类型
    private String feeTerminalId; //被计费用户的号码
    private byte tpPId; //GSM协议类型
    private byte tpUdhi; //
    private byte msgFmt; //信息格式
    private String msgSrc; //信息内容来源spId
    private String feeType;

    public CmppSubmitMessage(Handler handler, CmppHeader cmppHeader, ByteBuf bodyBuf) {
        super(handler);
        this.cmppHeader = cmppHeader;
        this.msgId = bodyBuf.readLong();
        this.pkTotal = bodyBuf.readByte();
        this.pkNumber = bodyBuf.readByte();
        this.registeredDelivery = bodyBuf.readByte();
        this.msgLevel = bodyBuf.readByte();
        this.serviceId = bodyBuf.readCharSequence(10, Charset.defaultCharset()).toString().trim();
        this.feeUserType = bodyBuf.readByte();
        this.feeTerminalId = bodyBuf.readCharSequence(21, Charset.defaultCharset()).toString().trim();

    }

    @Override
    public void doSomething(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public ByteBuf getMessageBuf() {
        return null;
    }
}
