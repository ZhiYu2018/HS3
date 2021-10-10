package com.czx.h3common.git;

import com.czx.h3common.git.impl.GitHubFileSystem;
import com.czx.h3common.git.impl.GiteeFileSystem;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import com.czx.h3outbound.ofs.vo.StorageType;
import com.czx.h3outbound.ofs.vo.UserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class HS3Storage {
    private UserInfo usi;
    private HS3FileSystem hs3FileSystem;
    public HS3Storage(UserInfo userInfo){
        usi = userInfo;
        if(usi.getType().equals(StorageType.ST_GITHUB)){
            hs3FileSystem = new GitHubFileSystem(usi);
        }else if(usi.getType().equals(StorageType.ST_GITEE)){
            hs3FileSystem = new GiteeFileSystem(usi);
        }else{
           throw HS3OfsExceptions.of(String.format("Type=[%s] is error", usi.getType().name()));
        }
    }
}
