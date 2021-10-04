package com.czx.h3facade.Exceptions;

import com.czx.h3facade.dto.Response;

public class ErrorHelper {
    public static void setResponse(Response<?> response, ErrorMsg errorMsg){
        response.setCode(errorMsg.getCode());
        response.setSubCode(errorMsg.getSubCode());
        response.setMsg(errorMsg.getMsg());
        response.setSubMsg(errorMsg.getSubMsg());
    }
}
