package com.tan.rabbitmq.basic;

import com.tan.rabbitmq.basic.header.HeaderRecv;
import com.tan.rabbitmq.basic.header.HeaderSend;
import com.tan.rabbitmq.basic.publishsubscribe.Publish;
import com.tan.rabbitmq.basic.publishsubscribe.Subscribe;
import com.tan.rabbitmq.basic.routing.RoutingRecv;
import com.tan.rabbitmq.basic.routing.RoutingSend;
import com.tan.rabbitmq.basic.rpc.RPCClient;
import com.tan.rabbitmq.basic.rpc.RPCServer;
import com.tan.rabbitmq.basic.topics.TopicsRecv;
import com.tan.rabbitmq.basic.topics.TopicsSend;
import com.tan.rabbitmq.basic.workqueues.WorkQueuesRecv;
import com.tan.rabbitmq.basic.workqueues.WorkQueuesSend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest

public class BasicTest {
    //测试线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    // rabbitmq的IP地址
    private final String rabbitmq_host = "127.0.0.1";
    // rabbitmq的用户名称
    private final String rabbitmq_user = "guest";
    // rabbitmq的用户密码
    private final String rabbitmq_pwd = "guest";

    @Test
    public void workQueues() throws InterruptedException {
        //接收端
        int recNum=2;
        while (recNum-- > 0) {
            final int recId=recNum;
            executorService.submit(()->{
                    WorkQueuesRecv.execute(rabbitmq_host,rabbitmq_user,rabbitmq_pwd,recId);
            });
        }

        Thread.sleep(5 * 1000);

        //发送端
        int sendNum=4;
        while (sendNum-- > 0) {
            executorService.submit(()->{
                WorkQueuesSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd);
            });
        }

        Thread.sleep(10 * 1000);
    }

    @Test
    public void publishSubscribe() throws InterruptedException {
        // 接收端
        int recNum = 2;
        while(recNum-- > 0) {
            final int recId = recNum;
            executorService.submit(() -> {
                Subscribe.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, recId);
            });
        }
        Thread.sleep(5* 100);
        // 发送端
        int sendNum = 2;
        while(sendNum-- > 0){
            executorService.submit(() -> {
                Publish.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd);
            });
        }

        // sleep 10s
        Thread.sleep(10 * 1000);
    }

    @Test
    public void routing_1() throws InterruptedException {
        //接收端1：绑定orange值
        executorService.submit(()->{
            String[] colours = {"orange"};
            RoutingRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, colours);
        });

        //接收端2：绑定green，black值
        executorService.submit(()->{
            String[] colours = {"black", "green"};
            RoutingRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, colours);
        });

        Thread.sleep(5 * 1000);

        //发送端，发送orange，只有接收端1会接收到
        executorService.submit(()->{
            String routing = "orange";
            RoutingSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routing);
        });

        //发送端，发送black，只有接收端2会接收到
        executorService.submit(()->{
            String routing = "black";
            RoutingSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routing);
        });

        Thread.sleep(10 * 1000);
    }

    @Test
    public void routing_2() throws InterruptedException {

        // 接收端：同时创建两个接收端，同时绑定black
        int recNum = 2;
        while(recNum-- > 0) {
            // 接收端：绑定 black 值
            executorService.submit(() -> {
                String[] colours = {"black"};
                RoutingRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, colours);
            });
        }

        Thread.sleep(5* 100);
        // 发送端1 ： 发送 black，所有的接收端都会收到
        executorService.submit(() -> {
            String routing = "black";
            RoutingSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routing);
        });

        // 发送端2 ： 发送 green，所有的接收端都不会收到
        executorService.submit(() -> {
            String routing = "green";
            RoutingSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routing);
        });

        // sleep 10s
        Thread.sleep(10 * 1000);
    }

    @Test
    public void topics() throws InterruptedException {
        // 消费者1：绑定 *.orange.* 值
        executorService.submit(() -> {
            String[] bindingKeys = {"*.orange.*"};
            TopicsRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, bindingKeys);
        });

        // 消费者2：绑定  "*.*.rabbit" 和 "lazy.#"值
        executorService.submit(() -> {
            String[] bindingKeys = {"*.*.rabbit", "lazy.#"};
            TopicsRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, bindingKeys);
        });

        Thread.sleep(5* 100);
        // 生产者1 ： 发送 quick.orange.rabbit，所有的接收端都会收到
        executorService.submit(() -> {
            String routing = "quick.orange.rabbit";
            TopicsSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routing);
        });

        // 生产者2 ： 发送 lazy.pink.rabbit，只有接收端2接收到
        executorService.submit(() -> {
            String routing = "lazy.pink.rabbit";
            TopicsSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, routing);
        });

        // sleep 10s
        Thread.sleep(10 * 1000);
    }

    @Test
    public void header() throws InterruptedException {

        // 消费者1：绑定 format=pdf,type=report
        executorService.submit(() -> {
            Map<String,Object> headers = new HashMap();
            headers.put("format","pdf");
            headers.put("type","report");
            headers.put("x-match","all");
            HeaderRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, headers);
        });

        // 消费者2：绑定  format=pdf,type=log
        executorService.submit(() -> {
            Map<String,Object> headers = new HashMap();
            headers.put("format","pdf");
            headers.put("type","log");
            headers.put("x-match","any");
            HeaderRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, headers);
        });

        // 消费者3：绑定  format=zip,type=report
        executorService.submit(() -> {
            Map<String,Object> headers = new HashMap();
            headers.put("format","zip");
            headers.put("type","report");
            headers.put("x-match","all");
            //   headers.put("x-match","any");
            HeaderRecv.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, headers);
        });

        Thread.sleep(2* 1000);

        System.out.println("=============消息1===================");
        // 生产者1 ： format=pdf,type=report
        executorService.submit(() -> {
            Map<String,Object> headers = new HashMap();
            headers.put("format","pdf");
            headers.put("type","report");
            HeaderSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, headers);
        });
        Thread.sleep(5* 100);

        System.out.println("=============消息2===================");
        // 生产者2 ： format=pdf
        executorService.submit(() -> {
            Map<String,Object> headers = new HashMap();
            headers.put("format","pdf");
            HeaderSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, headers);
        });

        Thread.sleep(5* 100);
        System.out.println("=============消息3===================");
        // 生产者1 ： format=zip,type=log
        executorService.submit(() -> {
            Map<String,Object> headers = new HashMap();
            headers.put("format","zip");
            headers.put("type","log");
            HeaderSend.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, headers);
        });

        // sleep 10s
        Thread.sleep(10 * 1000);
    }

    @Test
    public void rpc() throws InterruptedException {
        //rpc服务端先运行，创建队列
        executorService.submit(()->{
            RPCServer.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd);
        });

        //rpc客户端
        executorService.submit(()->{
            RPCClient.execute(rabbitmq_host, rabbitmq_user, rabbitmq_pwd, "rpc_test");
        });

        //sleep 10s
        Thread.sleep(10 * 1000);
    }
}


