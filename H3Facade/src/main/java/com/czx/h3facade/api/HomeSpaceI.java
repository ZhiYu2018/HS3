package com.czx.h3facade.api;

import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.Response;
import com.czx.h3facade.dto.SpaceDto;
import com.czx.h3facade.dto.UserTokenDto;
import com.czx.h3facade.vo.SpaceItemMeta;

import java.util.List;

public interface HomeSpaceI {
    Response<String> createSpace(Request<String> request);
    Response<List<String>> listHome(Request<UserTokenDto> request);
    Response<List<SpaceItemMeta>> listSpace(Request<SpaceDto> request);
}
