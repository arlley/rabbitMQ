package com.arlley.rabbitMQ.pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PubSubProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");
        factory.setPort(5672);

        Connection conn = factory.newConnection();

        Channel channel = conn.createChannel();

        channel.exchangeDeclare("pub", "fanout");
        for(int i=0;i<1000;i++) {
            channel.basicPublish("pub", "", null, ("pubSub模式"+i).getBytes());
        }
        channel.close();
        conn.close();
    }
}
