package com.czx.h3center.domain;

import com.czx.h3center.HS3Properties;
import com.czx.h3common.git.HS3Storage;
import com.czx.h3common.security.HSTink;
import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.api.CenterUserI;
import com.czx.h3facade.dto.*;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.vo.StorageType;
import com.czx.h3outbound.ofs.vo.UserInfo;
import com.czx.h3outbound.repository.dto.ConstantsValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("domainUserService")
@Slf4j
public class CenterUserImpl implements CenterUserI {
    @Autowired
    private HS3Storage hs3Storage;

    @Autowired
    private AccAggregator accAggregator;
    @Autowired
    private HSTink hsTink;

    @Override
    public Response<UserTokenDto> LogIn(Request<UserLoginDto> request) {
        Response<UserTokenDto> response = new Response<>();
        response.setBizNo(request.getBizNo());
        try{
            Account account = accAggregator.findAccount(request.getData());
            account.verifyLogin(request.getData());
            response.setData(account.createToken(hsTink));
            ErrorHelper.successResponse(response, "H3Center");
        }catch (H3RuntimeException exception){
            log.error("H3RuntimeException:{}", exception.getMessage());
            ErrorHelper.setResponse(response, exception.getErrorMsg());
        }
        return response;
    }

    @Override
    public Response<UserTokenDto> Register(Request<UserRegisterDto> request) {
        Response<UserTokenDto> response = new Response<>();
        response.setBizNo(request.getBizNo());
        try {
            Account account = accAggregator.createAccount(request.getData());
            response.setData(account.createToken(hsTink));
            ErrorHelper.successResponse(response, "H3Center");
        }catch (H3RuntimeException exception){
            log.error("H3RuntimeException:{}", exception.getMessage());
            ErrorHelper.setResponse(response, exception.getErrorMsg());
        }
        return response;
    }

    @Override
    public Response<String> ApplyHome(Request<ApplyHomeDto> request) {
        Response<String> response = new Response<>();
        response.setBizNo(request.getBizNo());
        try{
            Account account = accAggregator.getAccount(request.getData());
            if(!account.isOpenGit()) {
                UserInfo usi = UserInfo.builder().owner(HS3Properties.getOwner())
                        .repo(HS3Properties.getRepo()).token(HS3Properties.getToken())
                        .type(StorageType.ST_GITHUB).build();
                HS3FileSystem fileSystem = hs3Storage.getHs3FileSystem(usi);
                fileSystem.apply(request.getData().getName());
                request.getData().setGitAccount(HS3Properties.getOwner());
                request.getData().setGitPwd(HS3Properties.getToken());
                request.getData().setGitFlag(ConstantsValue.ACCOUNT_STATUS_ENABLE);
                account.setGitAccount(request.getData());
            }
            ErrorHelper.successResponse(response, "H3Center");
        }catch (H3RuntimeException exception){
            log.error("H3RuntimeException:{}", exception.getMessage());
            ErrorHelper.setResponse(response, exception.getErrorMsg());
        }
        return response;
    }
}
