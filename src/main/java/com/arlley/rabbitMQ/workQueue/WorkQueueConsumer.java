package com.arlley.rabbitMQ.workQueue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WorkQueueConsumer implements Runnable{

    private static ConnectionFactory factory = new ConnectionFactory();

    private static Connection connection;

    static {
        try {
            factory.setHost("localhost");
            factory.setUsername("admin");
            factory.setPassword("admin");
            factory.setPort(5672);
            factory.setVirtualHost("/");
            connection = factory.newConnection();
            //channel = connection.createChannel();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Thread thread1 = new Thread(new WorkQueueConsumer());
        Thread thread2 = new Thread(new WorkQueueConsumer());
        thread1.setName("thread1");
        thread2.setName("thread2");
        thread1.start();
        thread2.start();
        System.in.read();
    }

    @Override
    public void run() {
        try {
            final String name = Thread.currentThread().getName();
            final Channel channel = connection.createChannel();
            channel.basicQos(1);
            channel.basicConsume("workQueue", new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println("成功收到消息"+ name);
                    System.out.println(new String(message.getBody(), "UTF-8"));
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                }
            }, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {
                    channel.basicCancel(consumerTag);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
