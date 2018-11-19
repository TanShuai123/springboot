package com.tan.mvc.controller.auth;

import com.alibaba.fastjson.JSON;
import com.tan.mvc.pojo.RequestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/ws")
@Slf4j
public class AuthRabbitMQCtl {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 模拟登录
     *
     * @param request
     * @param name
     * @param pwd
     * @return
     */
    @PostMapping(value = "auth")
    public String auth(HttpServletRequest request, @RequestParam(required = true) String name, String pwd) {
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("loginName", name);
        return "websocket/sendtouser/ws-sendtouser-rabbitmq";
    }

    /**
     * 转到登录界面
     *
     * @return
     */
    @GetMapping(value = "login")
    public String login() {
        return "websocket/sendtouser/login";
    }

    /**
     * 发送页面
     *
     * @param msg
     * @param username
     * @return
     */
    @GetMapping(value = "send")
    public String sendMq2UserPage() {
        return "websocket/sendtouser/send";
    }

    @PostMapping(value = "send2user")
    @ResponseBody
    public int sendMq2User(String msg, String name) {
        log.info("msg={},name={}", msg, name);
        RequestMessage message = new RequestMessage();
        message.setName(msg);
        messagingTemplate.convertAndSendToUser(name, "/topic/demo", JSON.toJSONString(message));
        return 0;
    }
}
