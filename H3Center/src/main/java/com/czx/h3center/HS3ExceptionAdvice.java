package com.czx.h3center;

import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class HS3ExceptionAdvice {
    public HS3ExceptionAdvice(){
        log.info("HS3ExceptionAdvice init");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object handle(HttpServletRequest req, Exception ex){
        Response<Object> response = new Response<>();
        ErrorMsg msg;
        if(ex instanceof H3RuntimeException){
            msg = ((H3RuntimeException)ex).getErrorMsg();
        }else{
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            msg = ErrorMsg.builder().code(status.value()).sysServer("H3Center").subCode(status.name())
                    .msg(status.getReasonPhrase()).subMsg(ex.getMessage()).build();
        }

        ErrorHelper.setResponse(response, msg);
        return response;
    }
}
