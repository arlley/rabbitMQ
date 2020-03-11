package com.arlley.rabbitMQ.route;

import com.arlley.rabbitMQ.pubsub.PubSubConsumer;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RouteConsumer implements Runnable{

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

    public RouteConsumer(String routeKey){
        this.routeKey = routeKey;
    }

    @Override
    public void run() {
        try{
            final Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare().getQueue();
            final String threadName = Thread.currentThread().getName();
            channel.queueBind(queueName, "route", routeKey);
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
        for (int i=0; i<5; i++){
            Thread thread = new Thread(new RouteConsumer("my"));
            thread.setName("my"+i);
            thread.start();
        }

        for (int i=0; i<5; i++){
            Thread thread = new Thread(new RouteConsumer("notMy"));
            thread.setName("notMy"+i);
            thread.start();
        }
    }

}
