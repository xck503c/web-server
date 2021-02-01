package com.xck.socket.netscan.jpcap;

import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Classname NetSpeedMonitorResolver
 * @Description 流量统计，1s一次，单位KB/S
 * @Date 2021/1/31 19:11
 * @Created by xck503c
 */
public class NetSpeedMonitorResolver {

    private static AtomicBoolean sync = new AtomicBoolean(false);
    private static volatile int uploadSpeed = 0;
    private static volatile int downlowdSpeed = 0;
    private static volatile long lastSecond = System.currentTimeMillis();

    public static void monitor(Packet packet, String localIp){

        while (sync.compareAndSet(false, true)){
            Thread.yield();
        }

        try {
            long cur = System.currentTimeMillis();
            if(cur - lastSecond >= 1000){
                System.out.println(String.format("上传: %.2fKB/s, 下载: %.2fKB/s"
                        ,uploadSpeed/1024.0f, downlowdSpeed/1024.0f));

                lastSecond = cur;
                uploadSpeed = 0;
                downlowdSpeed = 0;
            }

            String srcIp = "";
            String dstIp = "";
            if (packet instanceof TCPPacket){
                srcIp = ((TCPPacket) packet).src_ip.toString();
                dstIp = ((TCPPacket) packet).dst_ip.toString();
            } else if (packet instanceof UDPPacket){
                srcIp = ((UDPPacket) packet).src_ip.toString();
                dstIp = ((UDPPacket) packet).dst_ip.toString();
            }

            if(!srcIp.equals("")){
                if(srcIp.equals(localIp)){
                    uploadSpeed += packet.caplen;
                }else if(dstIp.equals(localIp)){
                    downlowdSpeed += packet.caplen;
                }
            }
        } finally {
            sync.set(false);
        }
    }
}
