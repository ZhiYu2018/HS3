package com.czx.h3common.git.impl;

import com.czx.h3outbound.ofs.HS3File;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.vo.FileMeta;
import com.czx.h3outbound.ofs.vo.UserInfo;

import java.util.List;

public class GiteeFileSystem implements HS3FileSystem {
    private UserInfo usi;
    public GiteeFileSystem(UserInfo usi){
        this.usi = usi;
    }
    @Override
    public void apply(String home) {

    }

    @Override
    public void createDir(String home, String path) {

    }

    @Override
    public List<FileMeta> listHome(String home) {
        return null;
    }

    @Override
    public List<FileMeta> listSpace(String home, String path) {
        return null;
    }

    @Override
    public HS3File open(FileMeta fileMeta) {
        return null;
    }

    @Override
    public void clear() {

    }
}
