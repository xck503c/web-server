package com.xck.socket.netscan;


import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

/**
 * @Classname NetMonitor
 * @Description TODO
 * @Date 2021/1/31 19:11
 * @Created by xck503c
 */
public class NetMonitor {

    public static void main(String[] args) throws Exception {

        String targetIps = "/192.168.1.105";

        for (NetworkInterface networkInterface : JpcapCaptor.getDeviceList()) {
            NetworkInterfaceAddress[] addresses = networkInterface.addresses;
            if (networkInterface.addresses.length > 0) {
                for (NetworkInterfaceAddress address : addresses) {
                    if (targetIps.equals(address.address.toString())) {
                        JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(networkInterface, 65535, false, 20);
                        jpcapCaptor.loopPacket(-1, new Receiver());
                    }
                }
            }
        }
    }

    static class Receiver implements PacketReceiver {

        @Override
        public void receivePacket(Packet packet) {
            System.out.println(packet);
        }
    }


}
