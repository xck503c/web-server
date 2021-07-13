package com.xck.rabbitmq.quckstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者
 *
 * @author xuchengkun
 * @date 2021/07/12 13:26
 **/
public class Producer {

    public static void main(String[] args) throws Exception{

        //1. 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //配置
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/");

        //2. 通过连接工厂创建连接
        Connection connection = factory.newConnection();

        //3. 通过连接创建channel
        Channel channel = connection.createChannel();

        //4. 通过channel发送数据，不需要创建生产者角色
        String msg = "hello world";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish("", "test001", null, msg.getBytes());
        }

        //5. 记得关闭连接
        channel.close();
        connection.close();
    }
}
