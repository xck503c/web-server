import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class Consumer {
    private final static String submitSaveGroup = "submitSmsHisSaveConsumerGroup";

    @Test
    public void pushconsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(submitSaveGroup);
        consumer.setNamesrvAddr("49.235.32.249:9876");
        consumer.subscribe("submitSmsHisSave", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.printf("%s Receive New Message: %s %n", Thread.currentThread().getName()
                        , list);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    @Test
    public void consumer() throws Exception{
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(submitSaveGroup);
        consumer.setNamesrvAddr("49.235.32.249:9876");
        consumer.start();
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("submitSmsHisSave");
        for(MessageQueue queue : mqs){
            String topic = queue.getTopic();
            System.out.println(queue.getQueueId());
        }
        System.out.println(mqs);
    }
}
