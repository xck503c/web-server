package com.xck.persistentQueue.normal;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 普通读写
 *
 * @author xuchengkun
 * @date 2021/05/27 11:17
 **/
public class NormalPersistentQueue<E extends Serializable> extends AbstractQueue<E> {

    private String queueHomePath;
    private int dealbatchSize;
    private int seq = 0;
    private long writeTimeout;
    private BlockingQueue<E> writeBuffer = new ArrayBlockingQueue<>(5000);
    private BlockingQueue<E> readBuff = new ArrayBlockingQueue<>(5000);
    private volatile boolean isStop;
    private Thread flushBufferThread;
    private Thread readBufferThread;
    private AtomicLong writeSize = new AtomicLong(0L);

    public NormalPersistentQueue(String queueHomePath) {
        this.queueHomePath = queueHomePath;
        this.dealbatchSize = 500;
        this.writeTimeout = 2000;
        this.isStop = false;

        this.flushBufferThread = new Thread(new FlushTask());
        this.flushBufferThread.start();

        this.readBufferThread = new Thread(new ReadTask());
        this.readBufferThread.start();
    }

    @Override
    public boolean offer(E e) {
        if (!writeBuffer.offer(e)){
            flushBufferThread.interrupt();
            try {
                writeBuffer.put(e);
            } catch (InterruptedException e1) {
                //save
            }
        }

        return true;
    }

    @Override
    public E poll() {
        E e = readBuff.poll();
        if (e == null){
            e = writeBuffer.poll();
            if (e == null){
                readBufferThread.interrupt();
                try {
                    e = readBuff.take();
                } catch (InterruptedException e1) {
                    //
                }
            }
        }
        return e;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public int size() {
        return writeBuffer.size() + (int)writeSize.get() * dealbatchSize;
    }

    public void flush(List<E> list){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = sdf.format(System.currentTimeMillis()) + (seq++)%10000 + "_" + list.size();
        File file = new File(queueHomePath+"/"+fileName);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdir();
        }
//        System.out.println("write file " + file.getName());
        boolean isSuc = false;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(list);
            oos.flush();
            oos.close();
            isSuc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e1) {
            }
            if (isSuc){
                writeSize.incrementAndGet();
            }
        }
    }
    public List<E> read(){
        File dir = new File(queueHomePath);
        if (!dir.exists()){
            dir.mkdir();
        }
        File[] fileList = dir.listFiles();
        if (fileList.length == 0){
            return null;
        }
        List<File> files = new ArrayList<>();
        for (File f : fileList){
            files.add(f);
        }
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        boolean isSuc = false;
        List<E> list = null;
        for (File file : files){
            ObjectInputStream ois = null;
            try{
                ois = new ObjectInputStream(new FileInputStream(file));
                list = (List<E>)ois.readObject();
//                System.out.println("read file " + file.getName());
                isSuc = true;
                break;
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    if(ois != null){
                        ois.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (isSuc) {
                    file.delete();
                    writeSize.decrementAndGet();
                }
            }

        }
        return list;
    }

    /**
     * 刷新写缓冲区线程，刷新的条件有2个：
     * 1.缓冲区积压数量超过一定批次
     * 2.缓冲区积压数量未超过批次，但很长时间没刷了
     */
    private class FlushTask implements Runnable{
        @Override
        public void run() {
            long lastFlushTime = System.currentTimeMillis();
            while (!isStop){
                try{
//                    boolean isExceedDealBatchSize = writeBuffer.size() >= dealbatchSize;
////                    boolean isLongTimeNoFlush = (System.currentTimeMillis()-lastFlushTime)>writeTimeout;
////                    boolean isFlush = isExceedDealBatchSize || isLongTimeNoFlush;
////                    if (writeBuffer.isEmpty()){
//////                        Thread.sleep(100);
////                        continue;
////                    }
////                    if (!isFlush){
//////                        Thread.sleep(100);
////                        continue;
////                    }
                    List<E> batchList = new ArrayList<>();
                    for (int i = 0; i < dealbatchSize; i++) {
                        E e = writeBuffer.take();
                        if (e == null) continue;
                        batchList.add(e);
                    }
                    lastFlushTime = System.currentTimeMillis();
                    if (batchList.isEmpty()) continue;

                    flush(batchList);
                }catch (Exception e){
                }
            }
        }
    }

    /**
     * 填充读缓冲区线程：
     */
    private class ReadTask implements Runnable{
        @Override
        public void run() {
            while (!isStop){
                try{
                    int remainSize = readBuff.remainingCapacity();
                    if (remainSize < dealbatchSize && writeSize.get()>0){
//                        Thread.sleep(100);
                        continue;
                    }
                    List<E> list = read();
                    if (list == null || list.isEmpty()) {
//                        Thread.sleep(100);
                        continue;
                    }
                    for (E e : list) {
                        if (e == null) continue;
                        readBuff.put(e);
                    }
                }catch (InterruptedException e){
                }
            }
        }
    }

    public void stop(){
        this.isStop = true;
        flushBufferThread.interrupt();
        readBufferThread.interrupt();
    }

    public static void main(String[] args) {
        String path = "D:/home";
        NormalPersistentQueue<ArrayList> queue = new NormalPersistentQueue<>(path);
        List<ArrayList> list = new ArrayList<>();
        for (int i = 0; i<10; i++){
            ArrayList list1 = new ArrayList();
            list1.add("1");
            list.add(list1);
        }
        queue.flush(list);
        System.out.println(queue.read());;
    }
}
