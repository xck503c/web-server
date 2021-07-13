package com.xck.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * 生产者
 *
 * @author xuchengkun
 * @date 2021/07/13 13:36
 **/
public class Producer {

    public static void main(String[] args) throws Exception{

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        //指定消息确认模式
        channel.confirmSelect(); //开启确认模式

        String exchangeName = "test_confirm_exchange";
        String routingKey = "test.confirm";

        //添加确认监听器
        //deliveryTag
        channel.addConfirmListener(new ConfirmListener() {
            /**
             *
             * @param deliveryTag 消息投递唯一标识ID
             * @param multiple
             * @throws IOException
             */
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("-----------ack-----------");
                System.out.println("--ack-- "+deliveryTag + "-" + multiple + " --ack--");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.err.println("-----------no ack-----------");
                System.err.println("--no ack-- "+deliveryTag + "-" + multiple + " --no ack--");
            }
        });

        String msg = "hello world send confirm";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        }

        Thread.sleep(5000);

        channel.close();
        connection.close();
    }
}
