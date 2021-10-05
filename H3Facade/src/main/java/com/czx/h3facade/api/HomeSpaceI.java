package com.czx.h3facade.api;

import com.czx.h3facade.dto.*;
import com.czx.h3facade.vo.SpaceItemMeta;

import java.util.List;

public interface HomeSpaceI {
    Response<String> createSpace(Request<SpaceDto> request);
    Response<List<String>> listHome(Request<UserTokenDto> request);
    Response<List<SpaceItemMeta>> listSpace(Request<SpaceDto> request);
}
