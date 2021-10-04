package com.czx.h3facade.Exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMsg {
    private String sysServer;
    private int code;
    private String subCode;
    private String msg;
    private String subMsg;
}
