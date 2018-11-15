package com.tan.mvc.Interceptor;

import com.tan.mvc.pojo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyChannelInterceptorAdapter extends ImmutableMessageChannelInterceptor {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println(this.getClass().getCanonicalName() + " preSend");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        //检测用户订阅内容（防止用户订阅不合法频道）
        if (StompCommand.SUBSCRIBE.equals(command)) {
            System.out.println(this.getClass().getCanonicalName() + "用户订阅目的地：" + accessor.getDestination());
            return super.preSend(message, channel);
        }else{
            // 如果该用户订阅的频道不合法直接返回null前端用户就接受不到该频道信息
            System.out.println(this.getClass().getCanonicalName() + "用户订阅不合法频道");
            return super.preSend(message, channel);
        }
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        System.out.println(this.getClass().getCanonicalName() + " postSend");
        super.postSend(message, channel, sent);
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        System.out.println(this.getClass().getCanonicalName() + " postReceive");
        return super.postReceive(message, channel);
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        System.out.println(this.getClass().getCanonicalName() + " afterReceiveCompletion");
        super.afterReceiveCompletion(message, channel, ex);
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        System.out.println(this.getClass().getCanonicalName() +" afterSendCompletion");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (StompCommand.SUBSCRIBE.equals(command)){
            System.out.println(this.getClass().getCanonicalName() + " 订阅消息发送成功");
            simpMessagingTemplate.convertAndSend("/topic/getResponse",new ResponseMessage("消息发送成功"));
        }
        super.afterSendCompletion(message, channel, sent, ex);
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        System.out.println(this.getClass().getCanonicalName() + "preReceive");
        log.info("preReceive,channel={}", channel);
        return super.preReceive(channel);
    }
}
