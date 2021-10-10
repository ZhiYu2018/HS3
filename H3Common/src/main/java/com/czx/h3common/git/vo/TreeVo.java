package com.czx.h3common.git.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreeVo {
    private String owner;
    private String repo;
    private String path;
    private String base_sha;
    private TreeMode mode;
    private String content;
    private String returnSha;
}
