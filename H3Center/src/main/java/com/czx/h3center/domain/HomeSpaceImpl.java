package com.czx.h3center.domain;

import com.czx.h3center.HS3Properties;
import com.czx.h3common.git.HS3Storage;
import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.api.HomeSpaceI;
import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.Response;
import com.czx.h3facade.dto.SpaceDto;
import com.czx.h3facade.dto.UserTokenDto;
import com.czx.h3facade.vo.SpaceItemMeta;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.vo.FileMeta;
import com.czx.h3outbound.ofs.vo.StorageType;
import com.czx.h3outbound.ofs.vo.UserInfo;
import com.czx.h3outbound.repository.HomeNasDaoI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("domainHomeSpaceService")
@Slf4j
public class HomeSpaceImpl implements HomeSpaceI {
    @Autowired
    private HS3Storage hs3Storage;

    @Autowired
    private HomeNasDaoI homeNasDao;
    @Override
    public Response<String> createSpace(Request<SpaceDto> request) {
        Response<String> response = new Response<>();
        response.setBizNo(request.getBizNo());
        response.setData("createSpace success");
        HomeNas homeNas = HomeNas.getHomeNas(request.getData().getName(), homeNasDao);
        homeNas.addSpace(request.getData().getSpace());
        UserInfo usi = UserInfo.builder().owner(HS3Properties.getOwner())
                .repo(HS3Properties.getRepo()).token(HS3Properties.getToken())
                .type(StorageType.ST_GITHUB).build();
        HS3FileSystem fileSystem = hs3Storage.getHs3FileSystem(usi);
        fileSystem.createDir(request.getData().getName(), request.getData().getSpace());
        ErrorHelper.successResponse(response, "H3Center");
        return response;
    }

    @Override
    public Response<List<String>> listHome(Request<UserTokenDto> request) {
        Response<List<String>> response = new Response<>();
        response.setBizNo(request.getBizNo());
        HomeNas homeNas = HomeNas.getHomeNas(request.getData().getName(), homeNasDao);
        List<String> spaceList = homeNas.getSpace();
        response.setData(spaceList);
        ErrorHelper.successResponse(response, "H3Center");
        return response;
    }

    @Override
    public Response<List<SpaceItemMeta>> listSpace(Request<SpaceDto> request) {
        Response<List<SpaceItemMeta>> response = new Response<>();
        UserInfo usi = UserInfo.builder().owner(HS3Properties.getOwner())
                .repo(HS3Properties.getRepo()).token(HS3Properties.getToken())
                .type(StorageType.ST_GITHUB).build();
        HS3FileSystem fileSystem = hs3Storage.getHs3FileSystem(usi);
        List<FileMeta> fileMetaList = fileSystem.list(request.getData().getName(), request.getData().getSpace());

        List<SpaceItemMeta> spaceItemMetaList = fileMetaList.stream().map(m->{
            SpaceItemMeta meta = new SpaceItemMeta();
            int last = m.getPath().lastIndexOf("/");
            if(last > 0) {
                meta.setName(m.getPath().substring(last + 1));
            }
            meta.setPath(m.getPath());
            if(m.getSegments() != null) {
                meta.setSize(m.getSegments().size() * 1024*1024);
            }else{
                meta.setSize(0);
            }
            return meta;
        }).collect(Collectors.toList());

        response.setData(spaceItemMetaList);
        ErrorHelper.successResponse(response, "H3Center");
        return response;
    }
}
