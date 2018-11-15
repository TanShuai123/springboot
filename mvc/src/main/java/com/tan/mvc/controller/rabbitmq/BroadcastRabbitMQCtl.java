package com.tan.mvc.controller.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.tan.mvc.pojo.RequestMessage;
import com.tan.mvc.pojo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@Slf4j
public class BroadcastRabbitMQCtl {

    // 收到消息记数
    private AtomicInteger count = new AtomicInteger(0);

    @MessageMapping("/receive-rabbitmq")
    //@SendTo("/amq/queue/rabbitmq2")
    @SendTo("/exchange/rabbitmq/get-response")
    public ResponseMessage broadcast(RequestMessage requestMessage) {
        log.info("receive message = {}", JSONObject.toJSONString(requestMessage));
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("BroadcastRabbitMQCtl receive [" + count.incrementAndGet() + "] records");
        return responseMessage;
    }

    @RequestMapping("/broadcast-rabbitmq/index")
    public String broadcastIndex() {
        return "websocket/rabbitmq/ws-broadcast-rabbitmq";
    }

    @RequestMapping("/ipInfo")
    @ResponseBody
    public String ipInfo(HttpServletRequest request) {
        String ip = request.getRemoteUser();
        return ip;
    }
}
