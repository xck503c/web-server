package com.xck.queue.freelock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * 参照博客中第一个版本：https://www.cnblogs.com/hehe001/p/6334658.html
 * 论文来源：http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.53.8674&rep=rep1&type=pdf
 *
 *
 */
public class ValoisLinkedQueue<T> {

    private volatile Node<T> head;
    private volatile Node<T> tail;

    private static final sun.misc.Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            Class k = ConcurrentLinkedQueue.class;
            headOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ValoisLinkedQueue() {
        //有了一个空的头，这样可以保证tail指向的节点永远存在
        //我们称为dummy head
        head = new Node<>(null);
        tail = head;
    }

    public void enq(T t){
        Node<T> newNode = new Node<>(t);

        Node<T> oldTail;
        for(;;){
            oldTail = tail;
            boolean isSuc = oldTail.casNext(null, newNode); //挂到后面
            if (isSuc){
                casTail(oldTail, newNode); //更新tail指针，往后移动，失败也没关系
                break;
            }
            else{
                //这里会有过多的竞争 帮助移动，失败也无所谓
                //这里需要注意的是这块必不可少，因为如果某个线程挂了
                //，程序还可以继续运行
                oldTail.casNext(oldTail, oldTail.next);
            }
        }
    }

    public T deq(){
        Node<T> oldHead;
        for(;;){
            oldHead = head;
            if(oldHead.next == null) return null; //没有数据
            //这里会有个问题，那就是如果入队没有及时移动tail指针，会出现tail和head错位的情况
            if(casHead(oldHead, oldHead.next)) { //将头结点往后移动，移动成功则表示拿到数据
                return oldHead.next.item;
            }
        }
    }

    private boolean casHead(Node<T> oldNode, Node<T> newNode){
        return UNSAFE.compareAndSwapObject(this, headOffset, oldNode, newNode);
    }

    private boolean casTail(Node<T> oldNode, Node<T> newNode){
        return UNSAFE.compareAndSwapObject(this, tailOffset, oldNode, newNode);
    }

    private static class Node<T> {
        volatile T item;
        volatile Node<T> next;

        public Node(T item) { //初始化节点信息
            this.item = item;
            next = null;
        }

        boolean casItem(Node<T> oldNode, Node<T> newNode){
            return UNSAFE.compareAndSwapObject(this, itemOffset, oldNode, newNode);
        }

        boolean casNext(Node<T> oldNode, Node<T> newNode){
            return UNSAFE.compareAndSwapObject(this, nextOffset, oldNode, newNode);
        }

        //为了要实现cas，所以要初始化各个字段偏移量
        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;

        static {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                UNSAFE = (Unsafe) f.get(null);
                Class nodeClass = Node.class;
                itemOffset = UNSAFE.objectFieldOffset(
                        nodeClass.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset(
                        nodeClass.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e); //这里不加语法会有问题
            }
        }
    }

    public static void main(String[] args) throws Exception{
        final ValoisLinkedQueue<Integer> queue = new ValoisLinkedQueue<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Thread takeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                int count = 0;
                while (true){
                    try {
                        Integer i = queue.deq();
                        System.out.println("拿出 i=" + i);
                        if(i == null && count<10){
                            Thread.sleep(1000);
                            ++count;
                            continue;
                        }else if(count >= 10){
                            break;
                        }
                        ++size;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                countDownLatch.countDown();
                System.out.println(size);
            }
        });
        takeThread.start();

        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<100000; i++){
                    queue.enq(i);
                }
            }
        });
        putThread.start();

        countDownLatch.await();
    }
}
