package com.czx.h3center.controller;

import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.api.CenterUserI;
import com.czx.h3facade.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class CenterUserController implements CenterUserI{
    @Autowired
    @Qualifier("domainUserService")
    private CenterUserI centerUserImpl;

    @PostMapping("/login")
    @ResponseBody
    public Response<UserTokenDto> LogIn(@RequestBody Request<UserLoginDto> request) {
        if(request.getData() == null){
            Response<UserTokenDto> response = new Response<>();
            response.setBizNo(request.getBizNo());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ErrorMsg msg = ErrorMsg.builder().code(status.value()).msg(status.getReasonPhrase())
                    .subCode(status.name()).subMsg("data is null").build();
            ErrorHelper.setResponse(response, msg);
            return response;
        }

        log.info("LogIn: UserId={}, IdType={}", request.getData().getOpenId(), request.getData().getIdType());
        Response<UserTokenDto> response = centerUserImpl.LogIn(request);
        return response;
    }

    @PostMapping("/register")
    @ResponseBody
    public Response<UserTokenDto> Register(@RequestBody Request<UserRegisterDto> request) {
        if(request.getData() == null){
            Response<UserTokenDto> response = new Response<>();
            response.setBizNo(request.getBizNo());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ErrorMsg msg = ErrorMsg.builder().code(status.value()).msg(status.getReasonPhrase())
                    .subCode(status.name()).subMsg("data is null").build();
            ErrorHelper.setResponse(response, msg);
            return response;
        }
        log.info("Register: UserId={}, IdType={}", request.getData().getUserId(), request.getData().getIdType());
        Response<UserTokenDto> response = centerUserImpl.Register(request);
        return response;
    }

    @PostMapping("/applySpace")
    @ResponseBody
    public Response<String> ApplyHome(@RequestBody Request<ApplyHomeDto> request) {
        if(request.getData() == null){
            Response<String> response = new Response<>();
            response.setBizNo(request.getBizNo());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ErrorMsg msg = ErrorMsg.builder().code(status.value()).msg(status.getReasonPhrase())
                    .subCode(status.name()).subMsg("data is null").build();
            ErrorHelper.setResponse(response, msg);
            return response;
        }
        log.info("ApplyHome: UserId={}", request.getData().getName());
        return centerUserImpl.ApplyHome(request);
    }
}
