package com.xck.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * 消费者
 *
 * @author xuchengkun
 * @date 2021/07/12 13:26
 **/
public class TopicConsumer {

    public static void main(String[] args) throws Exception{

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_topic_exchange";
        String exchangeType = "topic";
        String queueName = "test_topic_queue";
//        String routingKey = "test.topic.#";
        String routingKey = "test.topic.*";

        //声明交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        //声明队列
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定
        channel.queueBind(queueName, exchangeName, routingKey);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, queueingConsumer);

        while (true){
            //8. 获取消息
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println(msg);
        }
    }
}
