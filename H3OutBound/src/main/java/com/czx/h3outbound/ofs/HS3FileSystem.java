package com.czx.h3outbound.ofs;

import com.czx.h3outbound.ofs.vo.FileMeta;

import java.util.List;

public interface HS3FileSystem {
    void apply(String home);
    void createDir(String home, String path);
    List<FileMeta> listHome(String home);
    List<FileMeta> listSpace(String home, String path);
    HS3File open(FileMeta fileMeta);
    void clear();
}
