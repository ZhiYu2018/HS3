package com.czx.h3facade.Exceptions;

import lombok.Data;

@Data
public class H3RuntimeException extends RuntimeException {
    private ErrorMsg errorMsg;
    public H3RuntimeException(ErrorMsg msg){
        super(msg.toString());
        this.errorMsg = msg;
    }
}
