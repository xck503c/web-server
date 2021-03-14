package com.xck.redis;

import com.xck.form.BlackMobile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.lang.reflect.ParameterizedType;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RedisSet<T extends RedisBytesParser> extends AbstractSet<T> {

    private RedisPool redisPool;

    private byte[] redisKey; //对应redis的集合key
    private List<T> datas = new ArrayList<>(); //缓存查询到的数据
    private int scanCount = 500; //每次扫描获取的数据量
    private ScanParams scanParams;

    public RedisSet(String redisKey, RedisPool redisPool) throws Exception{
        this.redisKey = redisKey.getBytes("utf-8");
        this.redisPool = redisPool;
        this.scanParams = new ScanParams();
        scanParams.count(scanCount);
    }

    @Override
    public Iterator<T> iterator() {
        return new RedisSetIterator<>();
    }

    @Override
    public int size() {
        return 0;
    }

    private final class RedisSetIterator<T extends RedisBytesParser> implements Iterator<T> {
        private List<T> datas = new ArrayList<>(); //缓存查询到的数据
        private byte[] cursor;
        private boolean isReadFirst; //是否已经scan过一次，用以判断redis游标归零的条件
        private int cacheDataCursor; //标识缓存数据的读取位置

        public RedisSetIterator() {
            this.cursor = ScanParams.SCAN_POINTER_START_BINARY;
            this.cacheDataCursor = 0;
            this.isReadFirst = false;
        }

        @Override
        public boolean hasNext() {
            //缓存读取完成
            boolean isReadCacheComplete = false;
            if (cacheDataCursor == 0 || cacheDataCursor == datas.size() - 1) {
                isReadCacheComplete = true;
            }
            //第一次已经操作过了，而且游标也是归零了，所以标识没有下个数据
            boolean isReadRedisComplete = false;
            if (isReadFirst && cursor.length == ScanParams.SCAN_POINTER_START_BINARY.length) {
                if (cursor[0] == ScanParams.SCAN_POINTER_START_BINARY[0]) {
                    isReadRedisComplete = true;
                }
            }

            return !(isReadCacheComplete & isReadRedisComplete);
        }

        @Override
        public T next() {

            if (cacheDataCursor < datas.size() - 1) {
                return datas.get(cacheDataCursor++);
            }

            if(datas.size() > 0) { //读完了重置
                datas.clear();
                cacheDataCursor = 0;
            }

            //重新读取
            Jedis jedis = null;
            try {
                jedis = redisPool.getJedis();
                if (jedis != null) {
                    ScanResult<byte[]> results = jedis.sscan(redisKey, cursor, scanParams);
                    cursor = results.getCursorAsBytes();

                    if(!isReadFirst) isReadFirst = true;

                    List<byte[]> resultList = results.getResult();
                    if (resultList != null && resultList.size() > 0) {
                        for (byte[] result : resultList) {
                            if(result == null) continue;
                            //泛型实例化
                            RedisBytesParser parser = (RedisBytesParser)newObject(this.getClass());
                            if(parser == null) {
                                StringBuilder sb = new StringBuilder();
                                for(byte b : result) sb.append(b).append(",");
                                sb.append("end");
                                System.out.println("parse redis blackMobile data error, bytes=" + sb.toString());
                            }
                            parser.parseBytes(result); // 初始化
                            datas.add((T)parser);
                        }

                        if (cacheDataCursor < datas.size() - 1) {
                            return datas.get(cacheDataCursor++);
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println("redis set error " + e);
            } finally {
                redisPool.returnJedis(jedis);
            }

            return null; //出现异常，可以休眠
        }
    }

    public static Object newObject(Class<?> clzz){
//        Object newObj = null;
//        try {
//            // 通过反射获取model的真实类型
//            ParameterizedType pt = (ParameterizedType) clzz.getGenericSuperclass();
//            Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0];
//            // 通过反射创建model的实例
//            newObj = clazz.newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return new BlackMobile();
    }
}
