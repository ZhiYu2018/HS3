package com.czx.h3common;

import com.czx.h3common.git.HS3Storage;
import com.czx.h3common.security.HSTink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class H3CommonConfigure {
    @Bean
    public HSTink getTink(){
        return new HSTink();
    }

    @Bean
    public HS3Storage getHS3Storage(){
        return new HS3Storage();
    }
}
