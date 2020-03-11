package com.arlley.rabbitMQ.pubsub;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PubSubConsumer implements Runnable{

    private static ConnectionFactory factory = new ConnectionFactory();
    private static Connection connection = null;

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

    @Override
    public void run() {
        try{
            final Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare().getQueue();
            final String threadName = Thread.currentThread().getName();
            channel.queueBind(queueName, "pub", "");
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

    public static void main(String[] args) throws IOException {
        for (int i=0; i<10; i++){
            Thread thread = new Thread(new PubSubConsumer());
            thread.setName("t"+i);
            thread.start();
        }
        System.out.println("按任意键退出！");
        System.in.read();
    }
}
