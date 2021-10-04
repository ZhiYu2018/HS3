package com.czx.h3outbound.repository;

import com.czx.h3outbound.repository.dto.OpenIdsDto;

public interface OpenIdsDaoI {
    OpenIdsDto findBy(String openId, Short type);
    int add(OpenIdsDto dto);
    int update(OpenIdsDto dto);
    int delete(String openId, Short type);
}
