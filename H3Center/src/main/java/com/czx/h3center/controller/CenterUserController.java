package com.czx.h3center.controller;

import com.czx.h3facade.api.CenterUserI;
import com.czx.h3facade.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        return null;
    }

    @PostMapping("/register")
    @ResponseBody
    public Response<UserTokenDto> Register(@RequestBody Request<UserRegisterDto> request) {
        if(request.getData() == null){

        }
        log.info("Register: UserId={}, IdType={}", request.getData().getUserId(), request.getData().getIdType());
        Response<UserTokenDto> response = centerUserImpl.Register(request);
        return response;
    }

    @PostMapping("/applySpace")
    @ResponseBody
    public Response<String> ApplyHome(@RequestBody Request<ApplyHomeDto> request) {
        return null;
    }
}
