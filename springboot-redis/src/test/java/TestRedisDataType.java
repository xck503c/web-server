import com.xck.form.BlackMobile;
import com.xck.redis.RedisBytesParser;
import com.xck.redis.RedisPool;
import com.xck.redis.RedisSet;
import org.junit.Test;

import java.util.Iterator;

public class TestRedisDataType {

    @Test
    public void testHmset() throws Exception{
        RedisPool pool = new RedisPool();

        String setRedisKey = "test:black:userlevel:xck";

        RedisSet<BlackMobile> set = new RedisSet<BlackMobile>(setRedisKey, pool);

        Iterator<BlackMobile> it = set.iterator();
        int i=0;
        while(it.hasNext()){
            System.out.println(it.next().mobile + " " + (++i));
        }
    }


}
