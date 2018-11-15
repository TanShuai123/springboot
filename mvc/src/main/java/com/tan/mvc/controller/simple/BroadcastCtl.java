package com.tan.mvc.controller.simple;

import com.alibaba.fastjson.JSONObject;
import com.tan.mvc.pojo.RequestMessage;
import com.tan.mvc.pojo.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class BroadcastCtl {

    private static final Logger logger = LoggerFactory.getLogger(BroadcastCtl.class);

    //收到记录的条数
    private AtomicInteger count = new AtomicInteger(0);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * @param requestMessage
     * @return
     * @MessageMapping 指定要接收消息的地址，类似@RequestMapping。除了注解到方法上，也可以注解到类上
     * @SendTo默认 消息将被发送到与传入消息相同的目的地
     * 消息的返回值是通过{@link org.springframework.messaging.converter.MessageConverter}进行转换
     */
    @MessageMapping("/receive")
    @SendTo("/topic/getResponse")
    public ResponseMessage broadcast(RequestMessage requestMessage) {
        logger.info("receive message={}", JSONObject.toJSONString(requestMessage));
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("BroadcastCtl receive [" + count.incrementAndGet() + "] records");
        return responseMessage;
    }

    @SubscribeMapping("/subscribe")
    public ResponseMessage subscribe() {
        //订阅提示消息
        logger.info("XXX用户订阅了我。。。");
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("感谢你的订阅");
        return responseMessage;
    }

    @RequestMapping(value = "/broadcast/index")
    public String broadcastIndex(HttpServletRequest request) {
        System.out.println(request.getRemoteHost());
        return "websocket/simple/ws-broadcast";
    }

    /*@RequestMapping("/broadcast/send")
    public void sendMessage(){
        messagingTemplate.convertAndSend("/topic/getResponse", new ResponseMessage("服务器主动推送数据"));
    }*/
}
