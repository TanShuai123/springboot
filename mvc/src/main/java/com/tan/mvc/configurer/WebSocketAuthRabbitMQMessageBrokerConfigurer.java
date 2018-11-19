package com.tan.mvc.configurer;

import com.tan.mvc.Interceptor.AuthHandShakeInterceptor;
import com.tan.mvc.handler.MyPrincipalHandShakeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketAuthRabbitMQMessageBrokerConfigurer extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private MyPrincipalHandShakeHandler myPrincipalHandShakeHandler;

    @Autowired
    private AuthHandShakeInterceptor authHandShakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/mq")
                .enableStompBrokerRelay("/exchange", "/topic", "/queue", "/amp/queue")
                .setRelayHost("192.168.1.116")
                .setClientLogin("admin")
                .setClientPasscode("admin")
                .setSystemLogin("admin")
                .setSystemPasscode("admin")
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/icc/websocket")
                .addInterceptors(authHandShakeInterceptor)
                .setHandshakeHandler(myPrincipalHandShakeHandler)
                .withSockJS();
    }
}
