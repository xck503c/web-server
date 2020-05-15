package com.xck;

public class Main {

    public static void main(String[] args) {
        InsertSubmitThread thread = new InsertSubmitThread();
        thread.start();

        ThreadPoolTransferThread thread1 = new ThreadPoolTransferThread(new MissionConfig());
        thread1.start();
    }
}
