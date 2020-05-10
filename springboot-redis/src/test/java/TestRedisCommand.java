import com.alibaba.fastjson.JSONObject;
import com.xck.form.TestClass;
import com.xck.redis.RedisPool;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRedisCommand {

    @Test
    public void testHmset(){
        RedisPool pool = new RedisPool();

        String hashKey = "test:hmset:user";

        Map<String, String> valuesMap = new HashMap<String, String>();
        for(int i=1; i<500; i++){
            String name = "xck" + i;
            valuesMap.put(name, JSONObject.toJSONString(new TestClass("徐成昆", 25)));
        }

        System.out.println(pool.hmset(hashKey, valuesMap));
    }

    @Test
    public void testhDel(){
        RedisPool pool = new RedisPool();

        String hashKey = "test:hmset:user";

        List<String> list = new ArrayList<String>();
        for(int i=0; i<500; i++){
            list.add("xck" + i);
        }

        System.out.println(pool.hdel(hashKey, list));
    }

    @Test
    public void testHdelPipeline(){
        RedisPool pool = new RedisPool();

        String hashKey = "test:hmset:user";

        List<String> list = new ArrayList<String>();
        for(int i=0; i<500; i++){
            list.add("xck" + i);
        }

        List<Long> results = new ArrayList<Long>();
        for(Object o : pool.hdelPipeline(hashKey, list)){
            results.add((Long)o);
        }
        System.out.println(results);
    }

    @Test
    public void testhmgetPipeline(){
        RedisPool pool = new RedisPool();

        //----
        String hashKey = "test:hmset:user";
        Map<String, String> valuesMap = new HashMap<String, String>();
        for(int i=1; i<250; i++){
            String name = "xck" + i;
            valuesMap.put(name, JSONObject.toJSONString(new TestClass("徐成昆"+i, i)));
        }

        System.out.println(pool.hmset(hashKey, valuesMap));
        //---

        //----
        String hashKey1 = "test:hmset:user1";
        Map<String, String> valuesMap1 = new HashMap<String, String>();
        for(int i=250; i<500; i++){
            String name = "xck" + i;
            valuesMap1.put(name, JSONObject.toJSONString(new TestClass("徐成昆"+i, i)));
        }

        System.out.println(pool.hmset(hashKey1, valuesMap1));
        //---

        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        map.put(hashKey, valuesMap);
        map.put(hashKey1, valuesMap1);
        List<Object> list = pool.hmgetPipeline(map);
        List<String> result = new ArrayList<String>();
        for(Object o : list){
            if(o instanceof List){
                result.addAll((List)o);
                System.out.println(o);
            }else {
                result.add((String)o);
            }
        }

        System.out.println(list.size());
        System.out.println(map);
        System.out.println(result.size());
    }
}
