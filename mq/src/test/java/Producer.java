import com.alibaba.fastjson.JSONObject;
import com.xck.rocketmq.SmsMessage;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;

import java.util.UUID;

public class Producer {
    private final static String submitSaveQueue = "submitSmsHisSaveSenderGroup";

    @Test
    public void syncSend() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer(submitSaveQueue);
        producer.setSendMsgTimeout(6000);
        producer.setNamesrvAddr("49.235.32.249:9876");
        producer.start();
        Message msg = new Message("submitSmsHisSave", getMsgJson());
        SendResult result = producer.send(msg);
        System.out.printf("%s%n", result);
        producer.shutdown();
    }

    public byte[] getMsgJson(){
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSubmit_sn(1160961902);
        smsMessage.setUser_sn(20698);
        smsMessage.setUser_id("wy8385");
        smsMessage.setService_code("106929038385");
        smsMessage.setExt_code("0");
        smsMessage.setTd_code("2020040837");
        smsMessage.setSp_number("1069070545090");
        smsMessage.setFilter_flag(0);
        smsMessage.setMobile("15720600000");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<71; i++) {
            sb.append("你");
        }
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
        smsMessage.setMsg_receive_time("2020-05-14 15:57:44,703");
        smsMessage.setMsg_deal_time("2020-05-14 15:57:44,703");
        smsMessage.setMsg_scan_time("2020-05-14 15:57:44,703");
        smsMessage.setMsg_send_time("2020-05-14 15:57:44,703");
        smsMessage.setMsg_report_time("2020-05-14 15:57:44,703");
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
