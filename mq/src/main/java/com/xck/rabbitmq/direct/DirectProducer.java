package com.xck.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * direct生产者
 *
 * @author xuchengkun
 * @date 2021/07/12 14:50
 **/
public class DirectProducer {

    public static void main(String[] args) throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        String exchange = "test_direct_exchange";
        String routingKey = "test.direct";

        String msg = "direct queue send";

        channel.basicPublish(exchange, routingKey, null, msg.getBytes());

        channel.close();
        connection.close();
    }
}
