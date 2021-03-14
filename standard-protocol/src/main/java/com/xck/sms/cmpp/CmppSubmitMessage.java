package com.xck.sms.cmpp;

import com.xck.sms.Handler;
import com.xck.sms.util.StringUtils;
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
    private byte msgFmt; //1B 信息格式
    private String msgSrc; //6B 信息内容来源spId
    private String feeType; //2B
    private String feeCode; //6B
    private String validTime;
    private String atTime;
    private String srcId;
    private int destUsrTl;
    private String destTerminalId;
    private int msgLength;
    private byte[] msgContent;
    private String reserve; //保留位置

    private String[] mobiles;

    public CmppSubmitMessage(Handler handler, CmppHeader cmppHeader, ByteBuf bodyBuf) throws Exception{
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
        this.tpPId = bodyBuf.readByte();
        this.tpUdhi = bodyBuf.readByte();
        this.msgFmt = bodyBuf.readByte();
        this.msgSrc = bodyBuf.readCharSequence(6, Charset.defaultCharset()).toString().trim();
        this.feeType = bodyBuf.readCharSequence(2, Charset.defaultCharset()).toString().trim();
        this.feeCode = bodyBuf.readCharSequence(6, Charset.defaultCharset()).toString().trim();
        this.validTime = bodyBuf.readCharSequence(17, Charset.defaultCharset()).toString().trim();
        this.atTime = bodyBuf.readCharSequence(17, Charset.defaultCharset()).toString().trim();

        byte[] b = new byte[21];
        bodyBuf.readBytes(b);
        this.srcId = StringUtils.readFromEndFlag(b, 0, 21, 0x00, null);

        this.destUsrTl = bodyBuf.readByte();
        if(destUsrTl < 0) destUsrTl+=256;

        this.mobiles = new String[1];
        for(int i=0; i<destUsrTl; i++){
            b = new byte[21];
            bodyBuf.readBytes(b);
            String mobile = StringUtils.readFromEndFlag(b, 0, 21, 0x00, "GBK");
            if(!"".equals(mobile)){
                mobiles[0] = mobile;
            }
        }

        this.msgLength = bodyBuf.readByte();
        if(msgLength < 0){
            msgLength+=256;
        }
        this.msgContent = bodyBuf.readBytes(msgLength).array();
        this.reserve = bodyBuf.readCharSequence(8, Charset.defaultCharset()).toString().trim();
    }

    @Override
    public void doSomething(ChannelHandlerContext ctx) throws Exception {
        System.out.println("收到提交的报文");
        CmppUserBean cmppUserBean = handler.getCmppUserBean();

    }

    @Override
    public ByteBuf getMessageBuf() {
        return null;
    }

    public String getMobile() {
        return mobiles[0];
    }
}
