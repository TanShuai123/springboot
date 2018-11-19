package com.tan.rabbitmq.basic.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RPCClient {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void execute(String host, String userName, String password, String message){
        // 配置连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(userName);
        factory.setPassword(password);

        Connection connection = null;
        Channel channel = null;
        try {
            // 建立TCP连接
            connection = factory.newConnection();
            // 在TCP连接的基础上创建通道
            channel = connection.createChannel();
            // 定义临时队列，并返回生成的队列名称
            String replyQueueName = channel.queueDeclare().getQueue();

            // 唯一标志本次请求
            String corrId = UUID.randomUUID().toString();
            //生成发送消息的属性
            AMQP.BasicProperties props=new AMQP.BasicProperties.Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();
            //发送消息，发送到默认交换机
            channel.basicPublish("", RPC_QUEUE_NAME, props, message.getBytes("UTF-8"));
            System.out.println("[RpcClient] Requesting :" + message);

            //阻塞队列，用于存储回调结果
            final BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
            //定义消息的回退方法
            channel.basicConsume(replyQueueName,true,new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(corrId)) {
                        responseQueue.offer(new String(body, "UTF-8"));
                    }
                }
            });
            //获取回调结果
            String result = responseQueue.take();
            System.out.println("[RpcClient] Result:" + result);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                // 空值判断，为了代码简洁略
                channel.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
