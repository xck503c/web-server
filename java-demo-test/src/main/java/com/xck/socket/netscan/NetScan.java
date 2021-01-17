package com.xck.socket.netscan;


import java.io.*;
import java.util.HashSet;
import java.util.Set;
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

        String ip3 = "192.168.1";
        final Set<String> ips = new HashSet<>();
        for (int j = 0; j < 255; j++) {
            final String ip = ip3 + "." + j;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    CmdResult cmdResult = execCmd("ping " + ip + " -n 4 -w 1000");
                    System.out.println("scan ip " + ip);
                    if (cmdResult.isSuc) {
                        System.out.println(cmdResult.result);
                        ips.add(ip);
                    }
                }
            });
        }
        System.out.println(ips);

        executor.shutdownNow();

        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("wait pool shutdown");
        }
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
            e.printStackTrace();
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
