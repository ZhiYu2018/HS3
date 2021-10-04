package com.czx.h3dao.repository;

import com.czx.h3dao.mapper.H3OpenIdsMapper;
import com.czx.h3dao.po.H3OpenIds;
import com.czx.h3dao.po.H3OpenIdsExample;
import com.czx.h3dao.util.Converter;
import com.czx.h3outbound.repository.OpenIdsDaoI;
import com.czx.h3outbound.repository.dto.ConstantsValue;
import com.czx.h3outbound.repository.dto.OpenIdsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class OpenIdsDaoImpl implements OpenIdsDaoI {
    @Autowired
    private H3OpenIdsMapper mapper;

    @Override
    public OpenIdsDto findBy(String openId, Short type) {
        H3OpenIdsExample example = new H3OpenIdsExample();
        example.createCriteria().andOpenIdEqualTo(openId).andIdTypeEqualTo(type).andStatusEqualTo(ConstantsValue.ACCOUNT_STATUS_ENABLE);
        List<H3OpenIds> h3OpenIdsList = mapper.selectByExample(example);
        if((h3OpenIdsList == null) || h3OpenIdsList.isEmpty()){
            log.info("Find none such Id={}, type={}", openId, type);
            return null;
        }
        return Converter.toOpenIdsDto(h3OpenIdsList.get(0));
    }

    @Override
    public int add(OpenIdsDto dto) {
        H3OpenIds openIds = Converter.toH3OpenIds(dto);
        openIds.setStatus(ConstantsValue.ACCOUNT_STATUS_ENABLE);
        openIds.setSysVer((int)System.currentTimeMillis()/1000);
        return mapper.insertSelective(openIds);
    }

    @Override
    public int update(OpenIdsDto dto) {
        H3OpenIds openIds = Converter.toH3OpenIds(dto);
        openIds.setSysVer((int)System.currentTimeMillis()/1000);
        openIds.setModifyTime(LocalDateTime.now());
        H3OpenIdsExample example = new H3OpenIdsExample();
        example.createCriteria().andOpenIdEqualTo(dto.getOpenId()).andIdTypeEqualTo(dto.getIdType());
        return mapper.updateByExampleSelective(openIds, example);
    }

    @Override
    public int delete(String openId, Short type) {
        H3OpenIdsExample example = new H3OpenIdsExample();
        example.createCriteria().andOpenIdEqualTo(openId).andIdTypeEqualTo(type);
        return mapper.deleteByExample(example);
    }
}
