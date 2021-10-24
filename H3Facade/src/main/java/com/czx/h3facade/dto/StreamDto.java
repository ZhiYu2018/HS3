package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class StreamDto {
    private String name;
    private String token;
    private String sessionKey;
    private String method;
    private byte [] buffer;
}
