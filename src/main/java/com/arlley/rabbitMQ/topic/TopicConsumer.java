package com.arlley.rabbitMQ.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicConsumer implements Runnable{
    private static ConnectionFactory factory = new ConnectionFactory();
    private static Connection connection = null;

    private String routeKey;

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

    public TopicConsumer(String routeKey){
        this.routeKey = routeKey;
    }

    @Override
    public void run() {
        try{
            final Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare().getQueue();
            final String threadName = Thread.currentThread().getName();
            channel.queueBind(queueName, "topic", routeKey);
            channel.basicConsume(queueName, new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery message) throws IOException {
                    System.out.println(threadName+"成功收到消息----------");
                    System.out.println(new String(message.getBody(), "UTF-8"));
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                }
            }, consumerTag -> {});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new TopicConsumer("lazy.#"));
        thread.setName("t1");
        thread.start();

        Thread thread1 = new Thread(new TopicConsumer("lazy.all.#"));
        thread1.setName("t2");
        thread1.start();
    }
}
