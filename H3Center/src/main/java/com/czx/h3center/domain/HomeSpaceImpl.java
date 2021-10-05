package com.czx.h3center.domain;

import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.api.HomeSpaceI;
import com.czx.h3facade.dto.*;
import com.czx.h3facade.vo.SpaceItemMeta;
import com.czx.h3outbound.repository.HomeNasDaoI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("domainHomeSpaceService")
@Slf4j
public class HomeSpaceImpl implements HomeSpaceI {
    @Autowired
    private HomeNasDaoI homeNasDao;
    @Override
    public Response<String> createSpace(Request<SpaceDto> request) {
        Response<String> response = new Response<>();
        response.setBizNo(request.getBizNo());
        response.setData("createSpace success");
        HomeNas homeNas = HomeNas.getHomeNas(request.getData().getName(), homeNasDao);
        homeNas.addSpace(request.getData().getSpace());
        ErrorHelper.successResponse(response, "H3Center");
        return response;
    }

    @Override
    public Response<List<String>> listHome(Request<UserTokenDto> request) {
        Response<List<String>> response = new Response<>();
        response.setBizNo(request.getBizNo());
        HomeNas homeNas = HomeNas.getHomeNas(request.getData().getName(), homeNasDao);
        List<String> spaceList = homeNas.getSpace();
        response.setData(spaceList);
        ErrorHelper.successResponse(response, "H3Center");
        return response;
    }

    @Override
    public Response<List<SpaceItemMeta>> listSpace(Request<SpaceDto> request) {
        Response<List<SpaceItemMeta>> response = new Response<>();
        HttpStatus status = HttpStatus.NOT_IMPLEMENTED;
        ErrorMsg msg = ErrorMsg.builder().code(status.value()).subCode(status.name()).msg(status.getReasonPhrase())
                .subMsg("This function is going").sysServer("H3Center").build();
        ErrorHelper.setResponse(response, msg);
        return response;
    }
}
