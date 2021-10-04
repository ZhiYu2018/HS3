package com.czx.h3center.domain;

import com.czx.h3facade.api.HomeSpaceI;
import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.Response;
import com.czx.h3facade.dto.SpaceDto;
import com.czx.h3facade.dto.UserTokenDto;
import com.czx.h3facade.vo.SpaceItemMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("domainHomeSpaceService")
@Slf4j
public class HomeSpaceImpl implements HomeSpaceI {
    @Override
    public Response<String> createSpace(Request<String> request) {
        return null;
    }

    @Override
    public Response<List<String>> listHome(Request<UserTokenDto> request) {
        return null;
    }

    @Override
    public Response<List<SpaceItemMeta>> listSpace(Request<SpaceDto> request) {
        return null;
    }
}
