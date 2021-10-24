package com.czx.h3center.controller;

import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.api.HomeSpaceI;
import com.czx.h3facade.dto.*;
import com.czx.h3facade.vo.SpaceItemMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nas")
@Slf4j
public class HomeSpaceController implements HomeSpaceI {
    @Autowired
    @Qualifier("domainHomeSpaceService")
    private HomeSpaceI homeSpaceImpl;

    @Override
    @PostMapping("/create_space")
    @ResponseBody
    public Response<String> createSpace(@RequestBody Request<SpaceDto> request) {
        if(request.getData() == null){
            Response<String> response = new Response<>();
            response.setBizNo(request.getBizNo());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ErrorMsg msg = ErrorMsg.builder().code(status.value()).msg(status.getReasonPhrase())
                    .subCode(status.name()).subMsg("data is null").build();
            ErrorHelper.setResponse(response, msg);
            return response;
        }

        log.info("createSpace: uid={}", request.getData().getName());
        return homeSpaceImpl.createSpace(request);
    }

    @Override
    @PostMapping("/list_home")
    @ResponseBody
    public Response<List<String>> listHome(@RequestBody Request<UserTokenDto> request) {
        if(request.getData() == null){
            Response<List<String>> response = new Response<>();
            response.setBizNo(request.getBizNo());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ErrorMsg msg = ErrorMsg.builder().code(status.value()).msg(status.getReasonPhrase())
                    .subCode(status.name()).subMsg("data is null").build();
            ErrorHelper.setResponse(response, msg);
            return response;
        }

        log.info("listHome: uid={}", request.getData().getName());
        return homeSpaceImpl.listHome(request);
    }

    @Override
    @PostMapping("/list_space")
    @ResponseBody
    public Response<List<SpaceItemMeta>> listSpace(@RequestBody Request<SpaceDto> request) {
        if(request.getData() == null){
            Response<List<SpaceItemMeta>> response = new Response<>();
            response.setBizNo(request.getBizNo());
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ErrorMsg msg = ErrorMsg.builder().code(status.value()).msg(status.getReasonPhrase())
                    .subCode(status.name()).subMsg("data is null").build();
            ErrorHelper.setResponse(response, msg);
            return response;
        }

        log.info("listSpace: uid={},path={}", request.getData().getName(), request.getData().getSpace());
        return homeSpaceImpl.listSpace(request);
    }
}
