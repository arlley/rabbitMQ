package com.arlley.rabbitMQ.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");
        factory.setPort(5672);

        Connection conn = factory.newConnection();

        Channel channel = conn.createChannel();

        channel.exchangeDeclare("topic", "topic");

        channel.basicPublish("topic","lazy.all.3", null, "topicAll".getBytes());

        channel.basicPublish("topic","lazy.eee", null, "topicE".getBytes());
        channel.close();
        conn.close();
    }
}
