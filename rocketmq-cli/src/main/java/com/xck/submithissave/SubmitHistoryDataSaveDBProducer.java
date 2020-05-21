package com.xck.submithissave;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class SubmitHistoryDataSaveDBProducer {
    private final static String submitSaveQueue = "HistoryDataSaveDBProducerGroup";

    private String namesrvAddr = "49.235.32.249:9876";

    private DefaultMQProducer defaultMQProducer;

    public void init(){
        defaultMQProducer = new DefaultMQProducer(submitSaveQueue);
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        defaultMQProducer.setSendMsgTimeout(6000);
        defaultMQProducer.setRetryTimesWhenSendFailed(3);
        defaultMQProducer.setDefaultTopicQueueNums(4);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            System.out.println("SubmitHistoryDataSaveDBProducer init error" + e);
        }
        System.out.println("SubmitHistoryDataSaveDBProducer init success");
    }

    public DefaultMQProducer getDefaultMQProducer(){
        return defaultMQProducer;
    }
}