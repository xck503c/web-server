package com.xck;

import com.xck.submithissave.SubmitHistoryDataSaveDBConsumer;

public class PushConsumerMain {

    private static SubmitHistoryDataSaveDBConsumer consumer = new SubmitHistoryDataSaveDBConsumer();

    public static void main(String[] args) {
        consumer.init();
    }
}
