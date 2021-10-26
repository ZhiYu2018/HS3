package com.czx.h3center.controller;

import com.czx.h3center.domain.HS3StreamImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
public class HS3OutStream extends AbstractWebSocketHandler {
    @Autowired
    private HS3StreamImpl hs3Stream;

    public HS3OutStream(){
    }

    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
    }

    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    }

    protected void handleTextMessage(WebSocketSession ws, TextMessage message) throws Exception {
        log.info("Remote={}, Msg:{}", ws.getId(), message.getPayload());
    }
}
