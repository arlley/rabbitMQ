package com.arlley.rabbitMQ.workQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WorkQueueProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setPort(5672);
        factory.setVirtualHost("/");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("workQueue", false, false, false, null);

        for(int i=0;i<1000;i++){
            String message = ("workQueue"+i);
            System.out.println("发送消息："+message+"------------");
            channel.basicPublish("", "workQueue", null, message.getBytes());
        }

        channel.close();
        connection.close();
    }
}
