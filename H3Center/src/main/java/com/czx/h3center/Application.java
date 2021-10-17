package com.czx.h3center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HS3Properties.class)
public class Application {
    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }
}
