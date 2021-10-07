package com.czx.h3common.git.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreeVo {
    private String owner;
    private String repo;
    private String path;
    private String salt;
    private String sha;
    private TreeMode mode;
    private byte [] content;
}
