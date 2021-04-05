package com.xck.socket.tftp.command;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * tftp交互命令
 */
public abstract class TFTPCommand {

    public final static byte zeroByte = 0;

    protected InetAddress ip;
    protected int port;
    protected int seq;

    public void assemble(String[] args) throws UnknownHostException {
        this.ip = InetAddress.getByName(args[1]);
        this.port = Integer.parseInt(args[2]);
        this.seq = 0;
    }

    public abstract void execute() throws SocketException, IOException;

    public enum TFTPOcode {
        READ(1), WRITE(2), DATA(3), ACK(4), ERROR(5);

        private int value;

        TFTPOcode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum TCTPMode {
        ASCII("netascii"), BINARY("octet");

        private String value;

        TCTPMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
