package com.xck.rocketmq.submithissave;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

public class SubmitHistoryDataSaveDBConsumer {
    private final static int cpu = Runtime.getRuntime().availableProcessors();
    private final static String submitSaveGroup = "submitSmsHisSaveConsumerGroup";

    private String namesrcAddr = "49.235.32.249:9876";

    private DefaultMQPushConsumer consumer;

    public void init(){
        consumer = new DefaultMQPushConsumer(submitSaveGroup);
        consumer.setNamesrvAddr(namesrcAddr);
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setPullBatchSize(10);
        consumer.setPullInterval(10);
        consumer.setConsumeThreadMax(2);
        consumer.setConsumeThreadMin(2);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.registerMessageListener(new SubmitHisDataInsertDBListener());
        try {
            consumer.subscribe("hisDataSaveDB", "submitSmsData");
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        System.out.println("SubmitHistoryDataSaveDBConsumer init success");
    }
}
