package com.xck;

import com.xck.submithissave.SubmitHistoryDataSaveDBPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.PullStatus;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PullConsumerMain {

    private static SubmitHistoryDataSaveDBPullConsumer consumer = new SubmitHistoryDataSaveDBPullConsumer();
    private static Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();
    private static AtomicLong num = new AtomicLong(0L);

    public static void main(String[] args) {
        consumer.init();
        DefaultMQPullConsumer pullConsumer = consumer.getConsumer();
        while (true) {
            try {
                //可以拿到消息队列的路由，包含主题，broker名字，queueid
                Set<MessageQueue> mqs = pullConsumer.fetchSubscribeMessageQueues("hisDataSaveDB");
                try {
                    for(MessageQueue mq : mqs){
                        PullResult pullResult = pullConsumer.pullBlockIfNotFound(mq, "submitSmsData"
                                , getMessageQueueOffset(mq), 10);
                        putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                        System.out.println("拉取结果: " + mq.getQueueId() + ", "
                                + pullResult.getNextBeginOffset() + ", " + pullResult.getMaxOffset());
                        if(PullStatus.FOUND.equals(pullResult.getPullStatus())){
                            List<MessageExt> list = pullResult.getMsgFoundList();
                            System.out.println(list.size());
                            num.getAndAdd(list.size());
                        }
                    }
                    System.out.println("消费数量: " + num.get());
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (MQClientException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offseTable.put(mq, offset);
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offseTable.get(mq);
        if (offset != null) {
            return offset;
        }
        return 0;

    }
}
