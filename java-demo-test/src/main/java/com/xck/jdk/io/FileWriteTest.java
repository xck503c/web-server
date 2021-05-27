package com.xck.jdk.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 测试文件写入性能
 *
 * @author xuchengkun
 * @date 2021/03/08 08:56
 **/
public class FileWriteTest {

    private static int writeTimes = 100000;
    private static String writeInfo = "remove cahce type=1, mobile=17800000000, userId=xck01111, level=3, blackType=52, addResult=true, failDesc=remove success\n";
    private static byte[] writeInfoBytes = null;

    static {
        try {
            for(int i=0; i<6; i++){
                writeInfo += writeInfo;
            }
            writeInfoBytes = writeInfo.getBytes("utf-8");
            System.out.println("测试字节长度: " + writeInfoBytes.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{

        for (int i=0; i<20; i++) {
            fileWriteTest(); //45612
            fileBufferWriteTest(8192*64);
            fileBufferWriteTest(8192*32);
            fileBufferWriteTest(8192*2);
            fileBufferWriteTest(8192);
//            fileBufferWriteTest(1024);
//            fileBufferWriteTest(64);
//            fileFileChannelWriteTest();
//            Thread.sleep(100);
//            fileFileChannelDirectWriteTest(); //36186
//            Thread.sleep(100);
        }
    }

    /**
     * 测试普通write性能
     */
    public static long fileWriteTime = 0L;
    public static long fileWriteTimes = 0L;
    public static void fileWriteTest() throws Exception{
        File file = new File("D:/testPerform1.test");
        if(file.exists()){
            file.delete();
        }

        FileOutputStream fos = new FileOutputStream(file);
        long start = System.currentTimeMillis();
        try {
            for(int i=0; i<writeTimes; i++){
                fos.write(writeInfoBytes);
            }
            fos.flush();
        } finally {
            fos.close();
        }
        ++fileWriteTimes;
        fileWriteTime+=(System.currentTimeMillis() - start);
        System.out.println("普通write 性能: " + fileWriteTime/fileWriteTimes);
    }

    /**
     * 测试带buffer的write性能
     */
    public static void fileBufferWriteTest(int bufferSize) throws Exception{
        File file = new File("D:/testPerform2.test"+bufferSize);
        if(file.exists()){
            file.delete();
        }

        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = null;
        if (bufferSize < 0) {
            bos = new BufferedOutputStream(fos);
        }else {
            bos = new BufferedOutputStream(fos, bufferSize);
        }
        long start = System.currentTimeMillis();
        try {
            for(int i=0; i<writeTimes; i++){
                bos.write(writeInfoBytes);
            }
            bos.flush();
        } finally {
            bos.close();
        }

        System.out.println("带buffer的write 性能: " + (System.currentTimeMillis() - start) + ", buffer大小=" + bufferSize);
    }

    /**
     * 测试文件通道的普通写入
     */
    public static void fileFileChannelWriteTest() throws Exception{
        File file = new File("D:/testPerform3.test");
        if(file.exists()){
            file.delete();
        }

        ByteBuffer b = ByteBuffer.allocate(writeInfoBytes.length);
        b.put(writeInfoBytes);
        FileOutputStream fos = new FileOutputStream(file);
        long start = System.currentTimeMillis();
        try {
            FileChannel fileChannel = fos.getChannel();
            for(int i=0; i<writeTimes; i++){
                fileChannel.write(b);
                b.clear();
            }
            fos.flush();
        } finally {
            fos.close();
        }
        System.out.println("channel 非直接缓冲区write 性能: " + (System.currentTimeMillis() - start));
    }

    /**
     * 测试文件通道直接缓冲区写入
     */
    public static long fileFileChannelDirectWriteTime = 0L;
    public static long fileFileChannelDirectWriteTimes = 0L;
    public static void fileFileChannelDirectWriteTest() throws Exception{
        File file = new File("D:/testPerform4.test");
        if(file.exists()){
            file.delete();
        }

        ByteBuffer b = ByteBuffer.allocateDirect(writeInfoBytes.length);
        b.put(writeInfoBytes);
        FileOutputStream fos = new FileOutputStream(file);
        long start = System.currentTimeMillis();
        try {
            FileChannel fileChannel = fos.getChannel();
            for(int i=0; i<writeTimes; i++){
                fileChannel.write(b);
                b.clear();
            }
            fos.flush();
        } finally {
            fos.close();
        }
        fileFileChannelDirectWriteTimes++;
        fileFileChannelDirectWriteTime+=(System.currentTimeMillis() - start);
        System.out.println("channel 直接缓冲区 write 性能: " + fileFileChannelDirectWriteTime/fileFileChannelDirectWriteTimes);
    }
}
