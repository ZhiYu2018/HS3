package com.czx.h3outbound.ofs;

import com.czx.h3outbound.ofs.vo.FileMeta;

import java.nio.ByteBuffer;

public interface HS3File {
    void open(String path);
    FileMeta meta();
    int read(ByteBuffer buffer, int len);
    int write(ByteBuffer buffer, int len);
    void close();
    void delete();
}
