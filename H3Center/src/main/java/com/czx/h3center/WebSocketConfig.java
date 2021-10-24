package com.czx.h3center;

import com.czx.h3center.controller.HS3InStream;
import com.czx.h3center.controller.HS3OutStream;
import com.czx.h3common.security.HSTink;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {
    private HSTink hsTink;

    @Autowired
    public WebSocketConfig(HSTink hsTink){
        this.hsTink = hsTink;
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(1024*8);
        container.setMaxBinaryMessageBufferSize(1024*8);
        return container;
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        policy.setInputBufferSize(1024*8);
        policy.setIdleTimeout(300000);
        return new DefaultHandshakeHandler(new JettyRequestUpgradeStrategy(policy));
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        HS3StreamInterceptor interceptor = new HS3StreamInterceptor(hsTink);
        registry.addHandler(getInHandler(), "stream/in").addInterceptors(interceptor);
        registry.addHandler(getOutHandler(), "stream/out").addInterceptors(interceptor);
    }

    @Bean
    public WebSocketHandler getInHandler(){
        return new HS3InStream();
    }

    @Bean
    public WebSocketHandler getOutHandler(){
        return new HS3OutStream();
    }
}
