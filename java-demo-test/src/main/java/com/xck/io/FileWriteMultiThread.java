package com.xck.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FileWriteMultiThread {

    private static CountDownLatch start = new CountDownLatch(8);
    private static CountDownLatch end = new CountDownLatch(8);

    public static void main(String[] args) throws Exception{
        File file = new File("/Users/xck/workDir/test20210312.txt");

        FileOutputStream fos = new FileOutputStream(file);
        for (int i = 0; i < 8; i++) {
            new WriteThread(fos, i).start();
        }

        end.await();
        fos.close();
    }

    public static class WriteThread extends Thread{

        private FileOutputStream fos;
        private int i;

        public WriteThread(FileOutputStream fos, int i) {
            this.fos = fos;
            this.i = i;
        }

        @Override
        public void run(){
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<20000; i++){
                sb.append(this.i);
            }
            sb.append("\n");
            try {
                for(int i=0; i<10000; i++){
                    fos.write(sb.toString().getBytes("utf-8"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            end.countDown();
        }
    }
}
