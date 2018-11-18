package com.tan.rabbitmq.advanced;

import com.tan.rabbitmq.advanced.publisherconfirm.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PublisherConfirmTest {

    private static final String routingKey = "publisher-confirm";

    //测试线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    // rabbitmq的IP地址
    private final String rabbitmq_host = "192.168.1.116";
    // rabbitmq的用户名称
    private final String rabbitmq_user = "admin";
    // rabbitmq的用户密码
    private final String rabbitmq_pwd = "admin";

    @Test
    public void publisherconfirm_noPublisherConfirmSend() throws InterruptedException {

        // 发送端
        executorService.submit(() -> {
            NoPublisherConfirmSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routingKey,1);
        });
        Thread.sleep(5* 100);

        // 消费端
        executorService.submit(() -> {
            PublisherConfirmRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routingKey);
        });
        Thread.sleep(5* 100);


        Thread.sleep(10 * 1000);
    }

    @Test
    public void publisherconfirm_transactional() throws InterruptedException {
        // 发送端
        executorService.submit(() -> {
            TransactionalSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routingKey,1);
        });
        Thread.sleep(5* 100);
    }

    @Test
    public void publisherconfirm_simpleConfirm() throws InterruptedException {

        // 发送端
        executorService.submit(() -> {
            SimpleConfirmSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routingKey,10);
        });
        Thread.sleep(10* 100);
    }

    @Test
    public void publisherconfirm_AsynConfirmSend() throws InterruptedException {
        // 发送端
        executorService.submit(() -> {
            AsynConfirmSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routingKey, 10);
        });

        // sleep 10s
        Thread.sleep(10 * 1000);
    }

}
