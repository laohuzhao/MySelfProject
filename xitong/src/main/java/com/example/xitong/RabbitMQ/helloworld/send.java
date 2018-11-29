package com.example.xitong.RabbitMQ.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class send {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] arg)
            throws Exception {
        /*创建连接*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");//配置工厂连接地址
        Connection connection = factory.newConnection();//工厂创建连接
        Channel channel = connection.createChannel();//连接创建通道
        //声明一个详细列队
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";
        //发送消息
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
