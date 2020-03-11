package com.arlley.rabbitMQ.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RpcConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");
        factory.setPort(5672);

        Connection conn = factory.newConnection();

        Channel channel = conn.createChannel();
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, "rpc", "rpcCall");

        channel.basicConsume(queueName, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body, "UTF-8"));
                String replyTo = properties.getReplyTo();
                channel.queueDeclare(replyTo, false, false, true, null);
                channel.queueBind(replyTo, "rpc", "reply");
                channel.basicPublish("rpc", "reply", properties, "2".getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
        System.in.read();
        channel.close();
        conn.close();
    }
}
