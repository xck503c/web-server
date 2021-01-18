package com.xck.socket.netscan;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Classname NetScan
 * @Description TODO
 * @Date 2021/1/17 17:32
 * @Created by xck503c
 */
public class NetScan {

    static BlockingQueue<Runnable> runnables = new ArrayBlockingQueue<Runnable>(100);
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4
            , 60, TimeUnit.SECONDS, runnables, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                runnables.put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public static void main(String[] args) throws Exception {

        List<String> segments = localSegment();
        Set<String> ips = scanIp(segments);

        while (runnables.size() > 0){
            Thread.sleep(1000);
        }

        executor.shutdown();
        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {}

        //扫描端口
        scanPort(ips, 80);
        scanPort(ips, 8080);
        scanPort(ips, 433);

        calJumpRouter(ips);
    }

    private static void calJumpRouter(Set<String> ips){
        for (String ip : ips){
            CmdResult result = execCmd("tracert -w 100 " + ip);
            System.out.println(result);
        }
    }

    private static Set<String> scanIp(List<String> segments){
        final Set<String> ips = new HashSet<>();
        for (String segment : segments) {
            System.out.println("扫描网段: " + segment);
            for (int j = 0; j < 255; j++) {
                final String ip = segment + "." + j;
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        CmdResult cmdResult = execCmd(pingCmdStr(ip));
                        if (cmdResult.isSuc) {
                            System.out.println("scan ip " + ip);
                            ips.add(ip);
                        }
                    }
                });
            }
        }
        System.out.println(ips);
        return ips;
    }

    private static void scanPort(Set<String> ips, int port){
        System.out.println("探测端口:" + port);
        for (String ip : ips) {
            try {
                Socket socket = new Socket();
                socket.setSoTimeout(200);
                socket.connect(new InetSocketAddress(ip, port), 200);
                if(socket.isConnected()){
                    System.out.println("ip=" + ip + ", 端口" + port + "开启");
                    OutputStream os = socket.getOutputStream();
                    os.write("ffff".getBytes());
                    os.flush();
                    InputStream is = socket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String len = null;
                    while ((len = br.readLine()) != null){
                        System.out.println(len);
                    }
                }

                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private static String pingCmdStr(String ip){
        String osType = System.getProperty("os.name").toLowerCase();
        if(osType.contains("windows")){
            return "ping " + ip + " -n 1 -w 500";
        }else{
            return "ping " + ip + " -c 1 -t 100 -W 100";
        }
    }

    private static List<String> localSegment() throws Exception{
        List<String> list = new ArrayList<>();
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            if (networkInterface.isVirtual() || !networkInterface.isUp()
                    || networkInterface.isLoopback()) {
                continue;
            }
            if (networkInterface.getDisplayName().contains("VirtualBox")
                    || networkInterface.getDisplayName().contains("VMware"))  {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress inetAddress = addresses.nextElement();
                if(inetAddress instanceof Inet4Address){
                    String address = inetAddress.getHostAddress();
                    int index = address.lastIndexOf(".");
                    list.add(address.substring(0, index));
                }
            }
        }
        return list;
    }

    private static CmdResult execCmd(String cmd){
        StringBuffer sb = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream is = process.getInputStream();
            BufferedReader bis = new BufferedReader(new InputStreamReader(is, "GBK"));
            String line = null;
            while ((line = bis.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (process.waitFor() == 0) {
               return new CmdResult(sb.toString(), true);
            }
            process.destroy();
        } catch (Exception e) {
            System.out.println("exec cmd=" + cmd + ", msg=" + e.getMessage());
        }
        return new CmdResult(sb.toString(), false);
    }

    private static class CmdResult{
        private String result;
        private boolean isSuc;

        public CmdResult(String result, boolean isSuc) {
            this.result = result;
            this.isSuc = isSuc;
        }

        @Override
        public String toString() {
            return "CmdResult{" +
                    "result='" + result + '\'' +
                    ", isSuc=" + isSuc +
                    '}';
        }
    }
}
