package com.xck.rocketmq;

import com.alibaba.fastjson.JSONObject;
import com.xck.rocketmq.submithissave.SubmitHistoryDataSaveDBProducer;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class ProducerMain {

    private static SubmitHistoryDataSaveDBProducer producer = new SubmitHistoryDataSaveDBProducer();

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        producer.init();
        for(int i=0; i<1; i++){
            new Thread(new ProducerRunnable()).start();
        }
    }

    public static class ProducerRunnable implements Runnable{

        public void run(){
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DefaultMQProducer mqProducer = producer.getDefaultMQProducer();
            int j =0;
            for (int count = 0; count<80; count++) {
                try {
                    List<Message> list = new ArrayList<Message>();
                    for(int i=0; i<10; i++){
//                        list.add(new Message("hisDataSaveDB", "submitSmsData", getMsgJson()));
                        String msgStr = ""+j;
                        list.add(new Message("hisDataSaveDB", "submitSmsData", msgStr.getBytes()));
                        j++;
                    }
                    SendResult result = mqProducer.send(list);
                    System.out.println("发送结果: " + result.getSendStatus().name()
                            + ", 主题: " + result.getMessageQueue().getTopic()
                            +", 入队: queueId=" + result.getMessageQueue().getQueueId()
                            + ", 放入borker: " + result.getMessageQueue().getBrokerName());
                    System.out.println("发送 " + sendnum.getAndAdd(list.size()));
                    Thread.sleep(100);
                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static AtomicLong mobile = new AtomicLong(15700000000L);
    private static AtomicLong sendnum = new AtomicLong(0L);

    public static byte[] getMsgJson(){
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSubmit_sn(1160961902);
        smsMessage.setUser_sn(20698);
        smsMessage.setUser_id("wy8385");
        smsMessage.setService_code("106929038385");
        smsMessage.setExt_code("0");
        smsMessage.setTd_code("2020040837");
        smsMessage.setSp_number("1069070545090");
        smsMessage.setFilter_flag(0);
        smsMessage.setMobile(mobile.getAndIncrement()+"");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<71; i++) {
            sb.append("你");
        }
        String time = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss,SSS");
        smsMessage.setMsg_content(sb.toString());
        smsMessage.setMsg_id(UUID.randomUUID().toString());
        smsMessage.setRpt_seq(2222222222222222222L);
        smsMessage.setStatus(1);
        smsMessage.setResponse(2);
        smsMessage.setTmp_msg_id(UUID.randomUUID().toString());
        smsMessage.setStat_flag(1);
        smsMessage.setSubmit_sn(-118);
        smsMessage.setPknumber(1);
        smsMessage.setPktotal(2);
        smsMessage.setPrice(0.032);
        smsMessage.setCountry_cn("中国人民共和国");
        smsMessage.setOri_mobile("15720000000");
        smsMessage.setCharge_count(1);
        smsMessage.setMsg_format(8);
        smsMessage.setErr("2");
        smsMessage.setMsg_receive_time(time);
        smsMessage.setMsg_deal_time(time);
        smsMessage.setMsg_scan_time(time);
        smsMessage.setMsg_send_time(time);
        smsMessage.setMsg_report_time(time);
        smsMessage.setCheck_user("system");
        smsMessage.setComplete_content(sb.toString());
        StringBuilder sb1 = new StringBuilder();
        for (int i=0; i<20; i++) {
            sb1.append("你");
        }
        smsMessage.addExtraField("key", sb1.toString());

        return JSONObject.toJSONString(smsMessage).getBytes();
    }
}
