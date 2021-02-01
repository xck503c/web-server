package com.xck.socket.netscan.jpcap;


import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

/**
 * @Classname NetSpeedMonitor
 * @Description 网速监控入口
 * @Date 2021/1/31 19:11
 * @Created by xck503c
 */
public class NetSpeedMonitor {

    public static String targetIps = "/192.168.118.114";

    /**
     * 表示你想要监控哪个ip
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        targetIps = args[0];

        for (NetworkInterface networkInterface : JpcapCaptor.getDeviceList()) {
            NetworkInterfaceAddress[] addresses = networkInterface.addresses;
            if (networkInterface.addresses.length > 0) {
                for (NetworkInterfaceAddress address : addresses) {
                    if (targetIps.equals(address.address.toString())) {
                        //这里会报错，忽略即可
                        JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(networkInterface, 2048, true, 1000);
                        //回调处理
                        jpcapCaptor.loopPacket(-1, new MonitorReceiver(targetIps));
                    }
                }
            }
        }
    }
}
