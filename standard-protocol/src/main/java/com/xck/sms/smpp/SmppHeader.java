package com.xck.sms.smpp;

import io.netty.buffer.ByteBuf;

public class SmppHeader {
    public static final int LEN_SMPP_HEADER = 16;
    private int total_length;
    private int command_id;
    private int command_status;
    private int sequence_id;

    public int getCommand_status() {
        return this.command_status;
    }

    public void setCommand_status(int command_status) {
        this.command_status = command_status;
    }

    public void setTotal_length(int len) {
        this.total_length = len;
    }

    public int getTotal_length() {
        return this.total_length;
    }

    public void setCommand_id(int id) {
        this.command_id = id;
    }

    public int getCommand_id() {
        return this.command_id;
    }

    public int getSequence_id() {
        return this.sequence_id;
    }

    public void setSequence_id(int sequence) {
        this.sequence_id = sequence;
    }

    public SmppHeader(ByteBuf buffer) {
        total_length = buffer.readInt();
    }
}
