package com.xck.submithissave;

import com.alibaba.fastjson.JSONObject;
import com.xck.SmsMessage;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SubmitHisDataInsertDBListener implements MessageListenerConcurrently {
    private AtomicLong receive = new AtomicLong();

    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        List<String> smsMessages = new ArrayList<String>();
        Map<Integer, DataBackLog> backLogMap = new HashMap<Integer, DataBackLog>();
        for(MessageExt msg : list){
//            String json = new String(msg.getBody());
            try {
//                SmsMessage sms = JSONObject.parseObject(json, SmsMessage.class);
                smsMessages.add(new String(msg.getBody()));
                DataBackLog dataBackLog = backLogMap.get(msg.getQueueId());
                if(dataBackLog == null){
                    backLogMap.put(msg.getQueueId(), dataBackLog = new DataBackLog(msg.getStoreHost().toString()));
                }
                long currentnewoffset = msg.getQueueOffset();
                long newmaxoffset = Long.parseLong(msg.getProperties().get(MessageConst.PROPERTY_MAX_OFFSET));
                dataBackLog.setOffset(currentnewoffset, newmaxoffset);
            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }

        System.out.println(smsMessages);
        System.out.println(Thread.currentThread().getName() + " 接收: " + smsMessages.size()
                + ", " + receive.addAndGet(smsMessages.size())
                + ", 积压: " + backLogMap);
        //入库
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
