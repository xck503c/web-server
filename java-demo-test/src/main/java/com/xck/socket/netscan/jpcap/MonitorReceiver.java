package com.xck.socket.netscan.jpcap;

import jpcap.PacketReceiver;
import jpcap.packet.Packet;

/**
 * @Classname MonitorReceiver
 * @Description 监控回调处理
 * @Date 2021/1/31 19:11
 * @Created by xck503c
 */
public class MonitorReceiver implements PacketReceiver {

    private String targetIps;

    public MonitorReceiver(String targetIps) {
        this.targetIps = targetIps;
    }

    @Override
    public void receivePacket(Packet packet) {
//        NetSpeedMonitorResolver.monitor(packet, targetIps);
    }
}
