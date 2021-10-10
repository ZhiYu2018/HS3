package com.czx.h3common.git.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileContentVo {
    private String parent;
    private String path;
    private String owner;
    private String repo;
    private String salt;
    private byte []content;
}
