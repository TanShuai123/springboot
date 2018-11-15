package com.tan.mvc.controller.simple;

import com.alibaba.fastjson.JSONObject;
import com.tan.mvc.pojo.RequestMessage;
import com.tan.mvc.pojo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
@Slf4j
public class BroadcastSingleCtl {

    private AtomicInteger count = new AtomicInteger(0);

    @MessageMapping("/receive-single")
    @SendToUser("/topic/getResponse")
    public ResponseMessage broadcast(RequestMessage requestMessage){
        log.info("receive message = {}" , JSONObject.toJSONString(requestMessage));
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMessage("BroadcastCtlSingle receive [" + count.incrementAndGet() + "] records");
        return responseMessage;
    }

    @RequestMapping(value="/broadcast-single/index")
    public String broadcastIndex(){
        return "websocket/simple/ws-broadcast-single";
    }
}
