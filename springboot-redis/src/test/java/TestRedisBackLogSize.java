import com.alibaba.fastjson.JSONObject;
import com.xck.form.TestClass;
import com.xck.redis.RedisPool;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRedisBackLogSize {

    private final static String listKey = "test:list:num";

    @Test
    public void testlpushMulti(){
        RedisPool pool = new RedisPool();

        int len = 0;
        List<String> list = new ArrayList<String>();
        for(int i=0; i<5000; i++){
            list.add(i+"");
            len += (i+"").getBytes().length;
        }

        System.out.println(listKey.getBytes().length);
        System.out.println(len);


        pool.lpushMultiPipe(listKey, list, 5000);
    }

    @Test
    public void testlpushs(){
        RedisPool pool = new RedisPool();

        List<String> list = new ArrayList<String>();
        for(int i=0; i<1000; i++){
            list.add(i+"");
        }

        pool.lpushMulit(listKey, list);
    }

    @Test
    public void testHmset() throws Exception{
        RedisPool pool = new RedisPool();

        String hashKey = "test:hmset:user";

        Map<String, String> valuesMap = new HashMap<String, String>();
        for(int i=1; i<10; i++){
            String name = "xck" + i;
            valuesMap.put(name, JSONObject.toJSONString(new TestClass("徐成昆", 25)));
        }
        pool.hmset(hashKey, valuesMap);
    }

    @Test
    public void testHset() throws Exception{
        RedisPool pool = new RedisPool();

        String hashKey = "test:hmset:user";

        Map<String, String> valuesMap = new HashMap<String, String>();
        for(int i=1; i<10; i++){
            String name = "xck" + i;
            valuesMap.put(name, JSONObject.toJSONString(new TestClass("徐成昆", 25)));
        }
        pool.hmset(hashKey, valuesMap);

    }
}
