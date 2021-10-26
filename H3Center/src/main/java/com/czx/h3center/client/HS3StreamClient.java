package com.czx.h3center.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

@Slf4j
public class HS3StreamClient {
    class ClientHandler extends TextWebSocketHandler {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage msg){
            log.info("Msg Return:{}", msg.getPayload());
        }
    }
    private JettyWebSocketClient wsClient;
    private String url;
    private WebSocketSession session;
    public HS3StreamClient(String uri){
        try {
            wsClient = new JettyWebSocketClient();
            wsClient.start();
            this.url = uri;
            log.info("Start ok");
        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void doHandler(Map<String, String> headers){
        URI uri = URI.create(url);
        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders();
        for(Map.Entry<String, String> kv:headers.entrySet()){
            wsHeaders.set(kv.getKey(), kv.getValue());
        }
        try{
            ListenableFuture<WebSocketSession> future = wsClient.doHandshake(new ClientHandler(), wsHeaders, uri);
            session = future.get();
            log.info("doHandler ok");
        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void sendMessage(ByteBuffer buffer, boolean isLast){
        if(!session.isOpen()){
            log.warn("Send msg failed:isOpen = {}", session.isOpen());
            return;
        }
        BinaryMessage bMsg = new BinaryMessage(buffer, isLast);
        try{
            session.sendMessage(bMsg);
        }catch (Exception ex){
            log.info("Send Msg exceptions:{}", ex.getMessage());
        }
    }

    public void sendMessage(String msg, boolean isLast){
        if(!session.isOpen()){
            log.warn("Send msg failed:isOpen = {}", session.isOpen());
            return;
        }
        TextMessage bMsg = new TextMessage(msg, isLast);
        try{
            session.sendMessage(bMsg);
        }catch (Exception ex){
            log.info("Send Msg exceptions:{}", ex.getMessage());
        }
    }

    public void stop(){
        wsClient.stop();
    }
}
