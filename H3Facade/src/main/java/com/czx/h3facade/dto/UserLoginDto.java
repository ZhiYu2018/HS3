package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private Short IdType;
    private String openId;
    private String salt;
    private String keySalt;
}
