package com.arlley.rabbitMQ.rpc;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RpcCaller {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/");
        factory.setPort(5672);

        Connection conn = factory.newConnection();

        Channel channel = conn.createChannel();
        channel.exchangeDeclare("rpc", "direct");
        String queueName = channel.queueDeclare().getQueue();

        String correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder().replyTo("replyTo").correlationId(correlationId).build();


        channel.basicPublish("rpc", "rpcCall", props, "1".getBytes());

        BlockingQueue<String> reponse = new ArrayBlockingQueue<String>(1);

        channel.basicConsume("replyTo", false, new DeliverCallback() {
            @Override
            public void handle(String consumerTag, Delivery message) throws IOException {
                System.out.println("收到回调结果");
                if(correlationId.equals(message.getProperties().getCorrelationId())){
                    String result = new String(message.getBody(), "UTF-8");
                    System.out.println(result);
                    reponse.offer(result);
                    channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                }else{
                    //收到的消息不是当前客户端需要的
                    channel.basicReject(message.getEnvelope().getDeliveryTag(), true);
                }
            }
        }, consumerTag -> {});

        String result = reponse.take();
        channel.close();
        conn.close();
    }
}
