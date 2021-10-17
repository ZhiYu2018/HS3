package com.czx.h3common.git;

import com.czx.h3common.git.impl.GitHubFileSystem;
import com.czx.h3common.git.impl.GiteeFileSystem;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import com.czx.h3outbound.ofs.vo.StorageType;
import com.czx.h3outbound.ofs.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HS3Storage {
    private Map<String,HS3FileSystem> fileSystemMap;
    public HS3Storage(){
        fileSystemMap = new ConcurrentHashMap<>();
    }
    public HS3FileSystem getHs3FileSystem(UserInfo usi){
        String key = String.format("%s.%s", usi.getOwner(), usi.getRepo());
        HS3FileSystem fileSystem = fileSystemMap.get(key);
        if(fileSystem != null){
            return fileSystem;
        }

        synchronized (this) {
            if (usi.getType().equals(StorageType.ST_GITHUB)) {
                fileSystem = new GitHubFileSystem(usi);
            } else if (usi.getType().equals(StorageType.ST_GITEE)) {
                fileSystem = new GiteeFileSystem(usi);
            } else {
                throw HS3OfsExceptions.of(String.format("Type=[%s] is error", usi.getType().name()));
            }
            fileSystemMap.put(key, fileSystem);
            log.info("Put key={}", key);
        }

        return fileSystem;
    }
}
