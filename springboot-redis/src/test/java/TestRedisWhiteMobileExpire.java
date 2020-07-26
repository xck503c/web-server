import com.xck.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestRedisWhiteMobileExpire {
    private static final SimpleDateFormat
            sdf = new SimpleDateFormat("YYYYMMddHH");


    @Test
    public void createData() throws Exception{
        RedisPool redisPool = new RedisPool();

        List<String> list = new ArrayList<String>(5000);
        int count = 1;
        for(long i=10000000000L; i<20000000000L; i++){
            if (i%5000 == 0) {
                Jedis jedis = redisPool.getJedis();
                Pipeline pipeline = jedis.pipelined();
                String hourTimeKey = "online_mobile:"+sdf.format(new Date());
                for (String mobile : list) {
                    String key = mobile.substring(1,5);
                    pipeline.setbit(key, Long.parseLong(mobile.substring(6)), true);
                    pipeline.setbit(hourTimeKey, Long.parseLong(mobile.substring(6)), true);
                }
                pipeline.sync();
                redisPool.returnJedis(jedis);
                System.out.println(count++);
                list.clear();
                Thread.sleep(10);
            }
            list.add(i+"");
        }
    }

    @Test
    public void createTime(){
        System.out.println(sdf.format(new Date()));
        System.out.println(sdf.format(new Date()));
    }

    public static class ExpireThread extends Thread{

        public ExpireThread(){

        }

        @Override
        public void run(){
            try {
                String hourTimeKey = "online_mobile:"+sdf.format(new Date());



                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
