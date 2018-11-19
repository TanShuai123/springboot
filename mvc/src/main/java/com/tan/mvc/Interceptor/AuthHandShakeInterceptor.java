package com.tan.mvc.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 自定义的握手拦截
 * 在握手前判断，判断当前用户是否已经登录。如果未登录，则不允许登录websocket
 */
@Component
@Slf4j
public class AuthHandShakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        HttpSession httpSession = getSession(serverHttpRequest);
        String user = (String) httpSession.getAttribute("loginName");

        if (StringUtils.isEmpty(user)) {
            log.error("未登录系统，禁止登录websocket");
            return false;
        }
        log.info("login={}", user);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            return serverHttpRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
