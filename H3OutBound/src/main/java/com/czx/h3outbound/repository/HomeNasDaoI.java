package com.czx.h3outbound.repository;

import com.czx.h3outbound.repository.dto.HomeNasDto;

import java.util.List;

public interface HomeNasDaoI {
    List<HomeNasDto> findByUid(String uid);
    int addOrUpdate(HomeNasDto homeNasDto);
}
