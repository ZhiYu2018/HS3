package com.czx.h3common.security.vo;

import lombok.Data;

@Data
public class SaltVo {
    private byte[] key;
    private byte[] iv;
    public SaltVo(){
        key = new byte[16];
        iv = new byte[32];
    }
}
