package com.xck.bdb;

public class BDBMain {

    public static void main(String[] args) {
        //String.class:value类型
        String path = System.getProperty("user.dir");
        BdbPersistentQueue<String> queue = new BdbPersistentQueue<String>(path + "/mq/bdb", "test", String.class);
        queue.offer("first");
        queue.offer("double");
        queue.offer("String");
        //获取移除队列
        String p1 = queue.poll();
        String p2 = queue.poll();
        System.out.println(p1);
        System.out.println(p2);
        //获取不移除队列--每次取出的都是第一个元素
        //String p1 = queue.peek();
        //String p2 = queue.peek();
    }
}
