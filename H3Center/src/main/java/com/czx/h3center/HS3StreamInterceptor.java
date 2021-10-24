package com.czx.h3center;

import com.czx.h3center.domain.Account;
import com.czx.h3common.security.HSTink;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.dto.UserTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.InetSocketAddress;
import java.util.Map;

@Slf4j
public class HS3StreamInterceptor implements HandshakeInterceptor {
    private static String X_UID = "X-HS3-UID";
    private static String X_TOKEN = "X-HS3-TOKEN";
    private static String X_SESSION_KEY = "X-HS3-S-KEY";
    private HSTink hsTink;
    public HS3StreamInterceptor(HSTink hsTink){
        this.hsTink = hsTink;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse rsp, WebSocketHandler wsh, Map<String, Object> map)
            throws Exception {
        InetSocketAddress remote = req.getRemoteAddress();
        log.info("beforeHandshake: Remote={}, Auth", remote.toString());
        HttpHeaders headers = req.getHeaders();
        String uid = getHttpHead(X_UID, headers);
        String token = getHttpHead(X_TOKEN, headers);
        String sessionKey = getHttpHead(X_SESSION_KEY, headers);
        if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(token) || StringUtils.isEmpty(sessionKey)){
            rsp.setStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            rsp.close();
            return false;
        }
        UserTokenDto dto = new UserTokenDto();
        dto.setSessionKey(sessionKey);
        dto.setToken(token);
        dto.setName(uid);
        try{
            Account.verifyToken(dto, hsTink);
        }catch (H3RuntimeException ex){
            log.warn("Auth uid={}, failed:{}",uid, ex.getErrorMsg());
            rsp.setStatusCode(HttpStatus.UNAUTHORIZED);
            rsp.close();
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest req, ServerHttpResponse rsp, WebSocketHandler wsh, Exception e) {
        InetSocketAddress remote = req.getRemoteAddress();
        log.info("afterHandshake: Remote={}", remote.toString());
    }


    private static String getHttpHead(String key, HttpHeaders headers){
        String o = headers.getFirst(key);
        log.info("Key={} get value={} from map", key, o);
        return o;
    }
}
