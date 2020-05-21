package com.xck.submithissave;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class SubmitHistoryDataSaveDBPullConsumer {
    private final static int cpu = Runtime.getRuntime().availableProcessors();
    private final static String submitSaveGroup = "submitSmsHisSaveConsumerGroup";

    private String namesrcAddr = "49.235.32.249:9876";

    private DefaultMQPullConsumer consumer;

    public void init(){
        consumer = new DefaultMQPullConsumer(submitSaveGroup);
        consumer.setNamesrvAddr(namesrcAddr);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        System.out.println("SubmitHistoryDataSaveDBPullConsumer init success");
    }

    public DefaultMQPullConsumer getConsumer() {
        return consumer;
    }
}
