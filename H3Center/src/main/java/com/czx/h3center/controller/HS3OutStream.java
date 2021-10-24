package com.czx.h3center.controller;

import com.czx.h3center.domain.HS3StreamImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class HS3OutStream extends TextWebSocketHandler {
    @Autowired
    private HS3StreamImpl hs3Stream;

    public HS3OutStream(){
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message){
    }
}
