package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class UserTokenDto {
    private String name;
    private String token;
    private String home;
    private String sessionKey;
}
