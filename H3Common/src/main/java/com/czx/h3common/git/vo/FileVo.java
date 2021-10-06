package com.czx.h3common.git.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileVo {
    private String owner;
    private byte[] contents;
    private String path;
    private String repo;
    private String sha;
}
