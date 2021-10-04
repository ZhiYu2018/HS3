package com.czx.h3center.domain;

import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.api.CenterUserI;
import com.czx.h3facade.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("domainUserService")
@Slf4j
public class CenterUserImpl implements CenterUserI {
    @Autowired
    private AccAggregator accAggregator;
    @Override
    public Response<UserTokenDto> LogIn(Request<UserLoginDto> request) {
        return null;
    }

    @Override
    public Response<UserTokenDto> Register(Request<UserRegisterDto> request) {
        Response<UserTokenDto> response = new Response<>();
        response.setBizNo(request.getBizNo());
        try {
            Account account = accAggregator.createAccount(request.getData());
        }catch (H3RuntimeException exception){
            log.error("H3RuntimeException:{}", exception.getMessage());
            ErrorHelper.setResponse(response, exception.getErrorMsg());
        }
        return response;
    }

    @Override
    public Response<String> ApplyHome(Request<ApplyHomeDto> request) {
        return null;
    }
}
