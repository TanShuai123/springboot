package com.tan.rabbitmq.basic.workqueues;

import com.rabbitmq.client.*;

import java.io.IOException;

public class WorkQueuesRecv {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void execute(String host, String username, String password, int id) {
        //配置连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        try {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println("[WorkQueueRecv-" + id + "] waitingForMessage");
            //设置每个客户端每次最多获取的消息数
            //channel.basicQos(1);
            final Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [WorkQueuesRecv-" +id+ "] Received '" + message + "'");
                    try{
                        doWork(message);
                    }finally{
                        System.out.println(" [WorkQueuesRecv-" +id+ "] Done");

                        //情况1:对处理好的消息进行应答
                        channel.basicAck(envelope.getDeliveryTag(),false);

                        //情况2:对于id=0的消费者者正常应答消息，其它id=0，拒绝此消息并要求重发
                        if (id == 0) {
                            channel.basicAck(envelope.getDeliveryTag(),false);
                        }else{
                            channel.basicReject(envelope.getDeliveryTag(),true);

                            //拒绝包含本条delivery_tag 所对应消息在内的所有比该值小的消息（除了已经被 ack 的以外)
                            channel.basicNack(envelope.getDeliveryTag(),false,false);
                        }
                    }
                }
            };
            //获取消息
            channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doWork(String task) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
