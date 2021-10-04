package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class Request <T>{
    private String bizNo;
    private String channel;
    private Long timestamp;
    private T data;
}
