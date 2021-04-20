package com.xck.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * rabbitmq helloworld send
 *
 * @author xuchengkun
 * @date 2021/03/30 18:35
 **/
public class Send {

    private final static String queueName = "hello";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("49.235.32.249");
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("my_vhost");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");


        channel.basicConsume(queueName, true, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("-------------consumer message-------------");
                System.out.println("consumerTag=" + consumerTag);
                System.out.println("envelope="+envelope);
                System.out.println("properties="+properties);
                System.out.println("msg="+new String(body));
            }
        });

        channel.close();
        connection.close();
    }
}
