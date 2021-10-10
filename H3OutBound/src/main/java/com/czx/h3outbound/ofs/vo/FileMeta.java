package com.czx.h3outbound.ofs.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class FileMeta {
    private String uid;
    private String path;
    private String salt;
    private String sha;
    private List<FileSegment> segments;
}
