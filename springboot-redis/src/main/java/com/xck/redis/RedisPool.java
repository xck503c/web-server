package com.xck.redis;

import com.xck.form.TestClass;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisPool {
    private JedisPool jedisPool = null;

    public void init(){
        if(jedisPool == null){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(60000);
            jedisPoolConfig.setMaxTotal(15);
            jedisPoolConfig.setMaxWaitMillis(10000);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPoolConfig.setMinIdle(60000);

            jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
        }
    }

    public Jedis getJedis(){
        if(jedisPool == null) init();
        return jedisPool.getResource();
    }

    public void returnJedis(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }

    //返回ok，批量插入
    public String hmset(String hashKey, Map<String, String> valueMap){
        String result = "";
        Jedis jedis = null;
        try{
            jedis = getJedis();
            result = jedis.hmset(hashKey, valueMap);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            returnJedis(jedis);
        }
        return result;
    }

    //如果是批量删除，返回值只会返回一个数字，表示删除的个数，无法判断哪个删除成功了
    //所以如果要利用删除的返回值，就只能使用pipeline进行批量的单个删除
    public Long hdel(String hashKey, List<String> fieldValues){
        Long result = 0L;
        Jedis jedis = null;
        String[] fieldArr = new String[fieldValues.size()];
        for(int i=0; i<fieldValues.size(); i++){
            fieldArr[i] = fieldValues.get(i);
        }
        try{
            jedis = getJedis();
            result = jedis .hdel(hashKey, fieldArr);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            returnJedis(jedis);
        }
        return result;
    }

    //pipeline删除，对于删除失败的会返回0，和传入的字段对应，主要用于需要返回值的情况
    //返回的每个元素都是一个Long
    public List<Object> hdelPipeline(String hashKey, List<String> fieldValues){
        List<Object> results = null;
        Jedis jedis = null;
        try{
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            for(int i=0; i<fieldValues.size(); i++){
                pipeline.hdel(hashKey, fieldValues.get(i));
            }
            results = pipeline.syncAndReturnAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            returnJedis(jedis);
        }
        return results;
    }

    //正常用pipeline比较好，但是可以看到这里为了测试塞入两个Map用于取出不同key的值
    //返回值是一个List<List>里面的每个元素都对应hmget的一个结果列表
    //当然也可以自己弄个结果集合利用Response<List<String>>
    public List<Object> hmgetPipeline(Map<String, Map<String, String>> fieldsMap){
        List<Object> results = null;
        Jedis jedis = null;
        try{
            List<Response<List<String>>> responses = new ArrayList<Response<List<String>>>();
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            for(String key : fieldsMap.keySet()){
                Map<String, String> tmpMap = fieldsMap.get(key);
                String[] fields = new String[tmpMap.size()];
                int i=0;
                for(String field : tmpMap.keySet()){
                    fields[i++] = field;
                }
                Response<List<String>> response = pipeline.hmget(key, fields);
                responses.add(response);
            }
            results = pipeline.syncAndReturnAll();
//            System.out.println(responses.get(0).get());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            returnJedis(jedis);
        }

        return results;
    }

    public void lpushMultiPipe(String listKey, List<String> list, int limit){
        Jedis jedis = null;
        try {
            if(limit < 4){
                limit = 4;
            }
            if(list.size()>limit){
                jedis = getJedis();
                Pipeline pipeline = jedis.pipelined();

                List<List<String>> allBatchList = new ArrayList<List<String>>();
                List<String> batchList = new ArrayList<String>();

                List<String> tmp = new ArrayList<String>(limit);
                for(int i=0; i<list.size(); i++){
                    tmp.add(list.get(i));
                    batchList.add(list.get(i));
                    if((i+1)%limit == 0){
                        pipeline.lpush(listKey, tmp.toArray(new String[tmp.size()]));
                        tmp.clear();
                        allBatchList.add(batchList);
                        batchList = new ArrayList<String>();
                    }
                }
                if(tmp.size()>0){
                    pipeline.lpush(listKey, tmp.toArray(new String[tmp.size()]));
                    tmp.clear();
                    allBatchList.add(batchList);
                }
                List<Object> results = pipeline.syncAndReturnAll();
                List<Object> deleteList = new ArrayList<Object>();
                for(int i=0; i<results.size(); i++){
                    Long r = (Long)results.get(i);
                    if(r > 0){
                        deleteList.add(allBatchList.get(i));
                    }
                }
                for(Object deleteO : deleteList){
                    allBatchList.remove(deleteO);
                }
                list.clear();
                for(List<String> l : allBatchList){
                    list.addAll(l);
                }
                if(list.size() > 0){
                    System.out.println(list.size() + " fail");
                }
            }else {
                String[] arr = new String[list.size()];
                for(int i=0; i<list.size(); i++){
                    arr[i] = list.get(i);
                }
                jedis = getJedis();
                jedis.lpush(listKey, arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnJedis(jedis);
        }
    }

    public List<String> popMulitTransac(String listKey, long count){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            long len = jedis.llen(listKey);
            if(len <= 0) return new ArrayList<String>();
            else if(len < count) count = len;

            if(count > len){

            }

            Transaction transaction = jedis.multi();
            Response<List<String>> response = transaction.lrange(listKey, -count, -1);
            //保留0~-count-1
            transaction.ltrim(listKey, 0, -count-1);
            transaction.exec();
            return response.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    public void lpushMulit(String listKey, List<String> list){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
            for(int i=0; i<list.size(); i++){
                pipeline.lpush(listKey, list.get(i));
            }
            pipeline.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnJedis(jedis);
        }
    }

    //取出来会有null的情况，即便用了llen判断也是一样
    public List<Object> rpopMulit(String listKey, long count){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Pipeline pipeline = jedis.pipelined();
//            Response<Long> l = pipeline.llen(listKey);
//            pipeline.sync();
//            long len = l.get();
//            if(len < 0) return new ArrayList<Object>();
//            else if(count > len){
//                count = len;
//            }
            for(int i=0; i<count; i++){
                pipeline.rpop(listKey);
            }
            return pipeline.syncAndReturnAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnJedis(jedis);
        }
        return null;
    }

    public long listLen(String listKey){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.llen(listKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnJedis(jedis);
        }
        return 0L;
    }

    public static void main(String[] args) {
        System.out.println(26276+13535+30509+30083+84628+174824
                +48872+32474+13069+44982+424678+129090+141576+261303+315665+113995
                +74988+321375+93941+40365+57183+6594+8187+162675+195823+102170
                +29411+78405+83915+254084);
    }
}
