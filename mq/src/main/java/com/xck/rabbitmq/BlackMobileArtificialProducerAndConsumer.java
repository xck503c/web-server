package com.xck.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import com.xck.db.DataSourceUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 黑名单人工数据生产者 造数据用
 *
 * @author xuchengkun
 * @date 2021/03/31 09:38
 **/
public class BlackMobileArtificialProducerAndConsumer {

    private static ConnectionFactory factory = null;

    private static AtomicInteger taskCount = new AtomicInteger();
    private static AtomicInteger deliverCount = new AtomicInteger();
    private static AtomicInteger sendCount = new AtomicInteger();
    private static List<String> list = new ArrayList<>();
    public static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(4000);
    public static ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() + 2, 60, TimeUnit.SECONDS, workQueue
            , new TaskPutBlockPolicy(workQueue));

    public static void main(String[] args) throws Exception{
        initFactory();

        MQChannel mqChannel = null;
        try {
            mqChannel = new MQChannel(factory, "hello");

            for(long mobile=15772846544L; mobile<=15772846544L+1000000; mobile++){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("mobile", mobile+"");
                mqChannel.send(jsonObject.toJSONString());
//                if (mobile % 5 == 3) {
//                    mqChannel.send(jsonObject.toJSONString());
//                }
                if (mobile == 15772846544L+500000) {
                    for (int i=0; i<600000; i++) {
                        mqChannel.send(jsonObject.toJSONString());
                    }
                }
                if (mobile % 1000 == 0){
                    Thread.sleep(400);
                    System.out.println(mobile);
                }
            }

            System.out.println("sleep 60s");
            Thread.sleep(60000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mqChannel != null){
                try {
                    mqChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        pool.shutdown();
        while (!pool.awaitTermination(5, TimeUnit.SECONDS)){

        }

        System.out.println("结束");
        System.out.println(deliverCount.get() + " " + sendCount.get());
    }

    public static void initFactory(){
        factory = new ConnectionFactory();
        factory.setHost("49.235.32.249");
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("my_vhost");
        factory.setPort(5672);
    }

    private static class MQChannel{
        private String queueName;
        private Connection connection;
        private Channel channel;

        public MQChannel(ConnectionFactory factory, String queueName) throws Exception{
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.queueName = queueName;
            this.channel.queueDeclare(queueName, false, false, false, null);
            recvCallBack();
        }

        public boolean send(String message){
            try {
                channel.basicPublish("", queueName, null, message.getBytes("utf-8"));
                sendCount.incrementAndGet();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void recvCallBack(){
            try {
                channel.basicConsume(queueName, true, "insertIntoBlackMobile", new DefaultConsumer(channel){
                    /**
                     *
                     * @param consumerTag 消费者标识，可以在消费的时候设置
                     * @param envelope 信封，标识信息用，比如说标识，是否重新发送标识，路由标识
                     * @param properties 消息的元数据信息
                     * @param body
                     * @throws IOException
                     */
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                        System.out.println(String.format("消费者标识:%s，投递消息标识:%s，是否重新投递:%b, 路由key:%s, 消息:%s"
//                                , consumerTag
//                                , envelope.getDeliveryTag(), envelope.isRedeliver(), envelope.getRoutingKey(), new String(body, Charset.forName("utf-8"))));
//                        System.out.println("properties="+properties);
                        deliverCount.incrementAndGet();
                        if(list.size() < 1000){
                            list.add(new String(body, Charset.forName("utf-8")));
                        }else {
                            if (!pool.isShutdown()) {
                                pool.submit(new InsertIntoBlack(list));
                                list.clear();
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() throws Exception{
            if (channel!=null) {
                channel.close();
            }
            if (connection!=null) {
                connection.close();
            }
        }
    }

    private static class InsertIntoBlack implements Runnable{

        private List<String> list = new ArrayList<>();

        public InsertIntoBlack(List<String> list) {
            this.list.addAll(list);
        }

        @Override
        public void run() {
            java.sql.Connection connection = null;
            PreparedStatement ps = null;
            try {
                connection = DataSourceUtil.getConnection();
                connection.setAutoCommit(false);
                ps = connection.prepareStatement("INSERT INTO `black_mobile` (`sn`, `mobile`, `insert_time`, `operator`, `level`, `td_code`, `user_id`, `type`, `black_type`, `black_level`, `status`, `update_time`) VALUES (null, ?, now(), 'test', '5', '', '00070007', '1', '53', '1', '1', '2020-10-25 14:43:00')");
                for (String jsonStr : list){
                    JSONObject jsonObject = JSON.parseObject(jsonStr);
                    ps.setString(1, (String) jsonObject.get("mobile"));
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
                taskCount.addAndGet(list.size());
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    if (connection!=null){
                        connection.rollback();
                    }
                } catch (SQLException e1) {
                }
            }finally {
                DataSourceUtil.freeConnection(connection, ps);
                System.out.println("执行数量: " + taskCount.get());
            }
        }
    }

    private static class TaskPutBlockPolicy implements RejectedExecutionHandler {

        public BlockingQueue<Runnable> workQueue;

        public TaskPutBlockPolicy(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    workQueue.put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
