package com.czx.h3center;

import com.czx.h3center.domain.Account;
import com.czx.h3common.security.HSTink;
import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.UserTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@Slf4j
@RestControllerAdvice
public class HS3RequestBodyAdvice extends RequestBodyAdviceAdapter {
    @Autowired
    private HSTink hsTink;

    public HS3RequestBodyAdvice(){
        log.info("HS3RequestBodyAdvice init");
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        boolean isType = (methodParameter.getParameterType().equals(Request.class));
        return isType;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        Request<Object> request = (Request<Object>)body;
        if(request.getData() instanceof UserTokenDto){
            log.info("verify token .......");
            Account.verifyToken((UserTokenDto)request.getData(), hsTink);
        }
        log.info("Do not need verify token .......");
        return body;
    }
}
