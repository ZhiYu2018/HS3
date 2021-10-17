package com.czx.h3center;

import com.czx.h3common.security.HSTink;
import com.czx.h3common.security.TinkAES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties()
@Component
@Slf4j
public class HS3Properties {
    private HSTink hsTink;

    private static String owner;
    private static String repo;
    private static String token;

    @Autowired
    public HS3Properties(HSTink hsTink){
        this.hsTink = hsTink;
    }

    public static String getOwner() {
        return owner;
    }

    @Value("${hs3storage.owner}")
    public void setOwner(String owner) {
        HS3Properties.owner = owner;
    }

    public static String getRepo() {
        return repo;
    }

    @Value("${hs3storage.repo}")
    public void setRepo(String repo) {
        HS3Properties.repo = repo;
    }

    public static String getToken() {
        return token;
    }

    @Value("${hs3storage.token}")
    public void setToken(String token) {
        try {
            TinkAES tinkAES = hsTink.getTinkAES();
            String raw = tinkAES.decrypt(token, "czx");
            log.info("Token={},raw={}", token, raw);
            HS3Properties.token = raw;
        }catch (Exception ex){
            log.warn("Aes token={} error:{}", token, ex);
            RuntimeException rex = new RuntimeException("decrypt token exceptions");
            rex.initCause(ex);
            throw rex;
        }
    }
}
