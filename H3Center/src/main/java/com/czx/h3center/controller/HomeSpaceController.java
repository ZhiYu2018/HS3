package com.czx.h3center.controller;

import com.czx.h3facade.api.HomeSpaceI;
import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.Response;
import com.czx.h3facade.dto.SpaceDto;
import com.czx.h3facade.dto.UserTokenDto;
import com.czx.h3facade.vo.SpaceItemMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public Response<String> createSpace(@RequestBody Request<String> request) {
        return null;
    }

    @Override
    @PostMapping("/list_home")
    @ResponseBody
    public Response<List<String>> listHome(@RequestBody Request<UserTokenDto> request) {
        return null;
    }

    @Override
    @PostMapping("/list_space")
    @ResponseBody
    public Response<List<SpaceItemMeta>> listSpace(@RequestBody Request<SpaceDto> request) {
        return null;
    }
}
