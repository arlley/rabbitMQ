package com.arlley.rabbitMQ.route;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RouteProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");
        factory.setPort(5672);

        Connection conn = factory.newConnection();

        Channel channel = conn.createChannel();

        channel.exchangeDeclare("route", "direct");

        channel.basicPublish("route", "my", null, "my".getBytes());

        channel.basicPublish("route", "notMy", null, "notMy".getBytes());

        channel.close();
        conn.close();
    }
}
