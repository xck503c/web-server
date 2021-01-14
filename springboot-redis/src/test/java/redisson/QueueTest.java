package redisson;

import com.xck.RunMain;
import com.xck.redis.RedissonPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname QueueTest
 * @Description TODO
 * @Date 2021/1/9 20:10
 * @Created by xck503c
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RunMain.class)
public class QueueTest {

    @Autowired
    public RedissonPool redissonPool;

    @Test
    public void main() throws Exception{
        redissonPool.setString("name", "xck");
//        putSms2RedisQueue();
//        putSms2RedisQueue();
//        putStringIntRedisQueue(99);

//        List<Object> outEle = redissonPool.outQueue("submitQueue", 1);
//        for(Object sms : outEle){
//            System.out.println(sms);
//        }
//        System.out.println(outEle.size());
        redissonPool.close();
    }

    public void putStringIntRedisQueue(int size) throws Exception{
        List<byte[]> list = new ArrayList<>();
        for(int i=90; i<size; i++){
            list.add((i+"").getBytes("utf-8"));
        }
        redissonPool.enQueue("submitQueue", list);
    }

    public void putSms2RedisQueue(){
        List<Sms> list = new ArrayList<>();
        long baseMobile = 15700000000L;
        for(int i=0; i<1; i++){
            list.add(new Sms(baseMobile+i+"", baseMobile+i+""));
        }
        redissonPool.enQueue("submitQueue", list);
    }

    public class Sms implements Serializable {

        long timeStamp;
        String mobile = "";
        String content = "";

        public Sms(String mobile, String content) {
            this.timeStamp = System.currentTimeMillis();
            this.mobile = mobile;
            this.content = content;
        }

        @Override
        public String toString() {
            return "{" +
                    "timeStamp=" + timeStamp +
                    ", mobile='" + mobile + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}