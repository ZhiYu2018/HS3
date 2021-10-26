package com.czx.h3center.controller;

import com.alibaba.fastjson.JSON;
import com.czx.h3center.domain.HS3StreamImpl;
import com.czx.h3facade.dto.HSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
public class HS3InStream extends AbstractWebSocketHandler {
    @Autowired
    private HS3StreamImpl hs3Stream;

    public HS3InStream(){

    }

    protected void handleBinaryMessage(WebSocketSession ws, BinaryMessage message) throws Exception {
        try{
            HSObject hsObject = JSON.parseObject(message.getPayload().array(), HSObject.class);
            log.info("Remote={}, Msg:U{}.S{}.F{}.N{}.L{}", ws.getId(), hsObject.getUid(), hsObject.getSpace(),
                    hsObject.getFile(), hsObject.getNumber(), hsObject.getContent().length);
            if(!hsObject.getIsLast()){
                hsObject.setIsLast(message.isLast());
            }
            hs3Stream.putLocalCache(hsObject);
        }catch (Exception ex){
            log.warn("Get error msg:{}", message.getPayload());
        }
    }

    protected void handlePongMessage(WebSocketSession ws, PongMessage message) throws Exception {
        log.info("Remote={}, Send a pong:{}", ws.getRemoteAddress(), message.toString());
        ws.sendMessage(message);
    }

    protected void handleTextMessage(WebSocketSession ws, TextMessage message) throws Exception {
        try{
            HSObject hsObject = JSON.parseObject(message.getPayload(), HSObject.class);
            log.info("Remote={}, Msg:U{}.S{}.F{}.N{}.L{}", ws.getId(), hsObject.getUid(), hsObject.getSpace(),
                     hsObject.getFile(), hsObject.getNumber(), hsObject.getContent().length);
            hs3Stream.putLocalCache(hsObject);
        }catch (Exception ex){
            log.warn("Get error msg:{}", message.getPayload());
        }
    }

}
