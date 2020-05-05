package com.xck.cmpp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 消息头，公共
 */
public class CmppHeader {
    public final static int HEADER_LEN = 12; //消息头长度固定12
    public final static int CONNRCT_BODY_LEN = 27;
    public final static int CONNRCT_RESP_BODY_LEN = 18;

    private int totalLenght; //消息总长度，含消息头和消息体
    private int commandId; //命令或者响应类型
    private int sequenceId; //消息流水号，顺序累加，步长为1，循环使用，应答和请求流水号相同

    public CmppHeader(int bodyLen, int commandId, int sequenceId){
        this.totalLenght = bodyLen + HEADER_LEN;
        this.commandId = commandId;
        this.sequenceId = sequenceId;
    }

    public CmppHeader(ByteBuf byteBuf) throws Exception{
        if(byteBuf.readableBytes() >= 12){
            this.totalLenght = byteBuf.readInt();
            if(totalLenght < 12){
                throw new Exception("总长度字段小于12");
            }
            this.commandId = byteBuf.readInt();
            this.sequenceId = byteBuf.readInt();
            ByteBuf bodyBuf = Unpooled.buffer(totalLenght - 12);
            byteBuf.readBytes(bodyBuf, 0);
        }else {
            throw new Exception("包长度小于12");
        }
    }

    public ByteBuf getHeaderBuf(){
        ByteBuf header = Unpooled.buffer(HEADER_LEN);
        header.writeInt(totalLenght);
        header.writeInt(commandId);
        header.writeInt(sequenceId);
        return header;
    }

    public int getTotalLenght() {
        return totalLenght;
    }

    public void setTotalLenght(int totalLenght) {
        this.totalLenght = totalLenght;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    @Override
    public String toString() {
        return "CmppHeader{" +
                "totalLenght=" + totalLenght +
                ", commandId=" + commandId +
                ", sequenceId=" + sequenceId +
                '}';
    }
}
