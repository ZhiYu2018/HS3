package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private Short idType;
    private String openId;
    private String salt;
    private String keySalt;
}
