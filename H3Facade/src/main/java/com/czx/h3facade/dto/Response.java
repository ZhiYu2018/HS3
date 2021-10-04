package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class Response <T>{
    private String bizNo;
    private Integer code;
    private String subCode;
    private String msg;
    private String subMsg;
    private T data;
}
