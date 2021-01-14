package redisson;

import com.xck.RunMain;
import com.xck.redis.RedissonPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RunMain.class)
public class CommandTest {

    @Autowired
    public RedissonPool redissonPool;

    @Test
    public void scan(){
        Set<String> keys = new HashSet<>();
        RKeys rKeys = redissonPool.getClient().getKeys();
        Iterator<String> it = rKeys.getKeysByPattern("q1:*", 1).iterator();
        while (it.hasNext()){
            String key = it.next();
            keys.add(key);
            System.out.println(key);
        }
        System.out.println(keys);
    }

    @Test
    public void hscan() throws Exception{

    }


}
