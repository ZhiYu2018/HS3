package com.czx.h3center.domain;

import com.czx.h3common.security.H3SecurityUtil;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3outbound.repository.HomeNasDaoI;
import com.czx.h3outbound.repository.dto.HomeNasDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HomeNas {
    private String uid;
    private HomeNasDaoI homeNasDao;
    private HomeNas(String uid, HomeNasDaoI homeNasDao){
        this.uid = uid;
        this.homeNasDao = homeNasDao;
    }
    public static HomeNas getHomeNas(String uid, HomeNasDaoI homeNasDao){
        return new HomeNas(uid, homeNasDao);
    }

    public List<String> getSpace(){
        List<HomeNasDto> homeNasDtoList = homeNasDao.findByUid(uid);
        List<String> list = new ArrayList<>();
        if(homeNasDtoList != null){
            for (HomeNasDto d:homeNasDtoList){
                list.add(d.getHome());
            }
        }
        return list;
    }

    public void addSpace(String space){
        List<HomeNasDto> homeNasDtoList = homeNasDao.findByUid(uid);
        if(homeNasDtoList != null){
            for(HomeNasDto d: homeNasDtoList){
                if(d.getHome().equals(space)){
                    log.info("Space={}.{} is exist", uid, space);
                    return;
                }
            }
            if(homeNasDtoList.size() >= 8){
                HttpStatus status = HttpStatus.FORBIDDEN;
                ErrorMsg msg = ErrorMsg.builder().code(status.value()).subCode(status.name())
                        .msg(status.getReasonPhrase()).subMsg("Too many spaces").sysServer("H3Center").build();
                throw new H3RuntimeException(msg);
            }
        }

        String salt = H3SecurityUtil.getSalt();
        HomeNasDto dto = HomeNasDto.builder().home(space).uid(uid).salt(salt).build();
        int r = homeNasDao.addOrUpdate(dto);
        log.info("add space={}.{} return:{}", uid, space, r);
        return;
    }
}
