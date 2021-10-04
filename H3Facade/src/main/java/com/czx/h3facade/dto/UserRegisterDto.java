package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String userId;
    private Short idType;
    private String pwd;
    private String key;
}
