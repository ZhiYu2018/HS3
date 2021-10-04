package com.czx.h3dao.repository;

import com.czx.h3dao.mapper.H3HomeSpaceMapper;
import com.czx.h3dao.po.H3HomeSpace;
import com.czx.h3dao.po.H3HomeSpaceExample;
import com.czx.h3outbound.repository.HomeNasDaoI;
import com.czx.h3outbound.repository.dto.HomeNasDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class HomeNasDaoImpl implements HomeNasDaoI {
    @Autowired
    H3HomeSpaceMapper mapper;
    @Override
    public List<HomeNasDto> findByUid(String uid) {
        H3HomeSpaceExample example = new H3HomeSpaceExample();
        example.createCriteria().andUidEqualTo(uid);
        List<H3HomeSpace> homeSpaceList = mapper.selectByExample(example);
        if(homeSpaceList == null){
            return null;
        }

        List<HomeNasDto> homeNasDtoList = new ArrayList<>();
        for(H3HomeSpace hs:homeSpaceList){
            HomeNasDto hd = HomeNasDto.builder().uid(uid).home(hs.getHomeName()).salt(hs.getHomeSalt()).build();
            homeNasDtoList.add(hd);
        }
        return homeNasDtoList;
    }

    @Override
    public int addOrUpdate(HomeNasDto homeNasDto) {
        H3HomeSpace homeSpace = new H3HomeSpace();
        homeSpace.setUid(homeNasDto.getUid());
        homeSpace.setHomeName(homeNasDto.getHome());
        homeSpace.setHomeSalt(homeNasDto.getSalt());
        try{
            return mapper.insertSelective(homeSpace);
        }catch (DuplicateKeyException ex){
            log.info("Key=[{},{}] is exist", homeNasDto.getUid(), homeNasDto.getHome());
            homeSpace.setModifyTime(LocalDateTime.now());
            return mapper.updateByPrimaryKeySelective(homeSpace);
        }
    }
}
