package com.czx.h3facade.Exceptions;

import com.czx.h3facade.dto.Response;

public class ErrorHelper {
    public static void setResponse(Response<?> response, ErrorMsg errorMsg){
        response.setCode(errorMsg.getCode());
        response.setSubCode(String.format("%s.%s", errorMsg.getSysServer(),errorMsg.getSubCode()));
        response.setMsg(errorMsg.getMsg());
        response.setSubMsg(errorMsg.getSubMsg());
    }

    public static void successResponse(Response<?> response, String sysId){
        response.setCode(200);
        response.setMsg("Success");
        response.setSubCode(sysId + ".SUCCESS");
        response.setSubMsg("Success");
    }
}
