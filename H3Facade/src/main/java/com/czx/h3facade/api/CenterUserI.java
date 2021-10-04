package com.czx.h3facade.api;

import com.czx.h3facade.dto.*;

public interface CenterUserI {
    Response<UserTokenDto> LogIn(Request<UserLoginDto> request);
    Response<UserTokenDto> Register(Request<UserRegisterDto> request);
    Response<String> ApplyHome(Request<ApplyHomeDto> request);
}
