package com.xck.socket.netscan.jpcap;

import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

public class UDPResolver {

    private UDPPacket tcpPacket;

    public UDPResolver(UDPPacket tcpPacket){
        this.tcpPacket = tcpPacket;
    }

    public void resolve(){

        System.out.println(String.format("源:%s:%d --> 目标%s:%d"
                , tcpPacket.src_ip, tcpPacket.src_port, tcpPacket.dst_ip, tcpPacket.dst_port));

    }
}
