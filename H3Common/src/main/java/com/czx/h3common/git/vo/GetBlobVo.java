package com.czx.h3common.git.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBlobVo {
    private String owner;
    private String repo;
    private String sha;
    private String salt;
    private byte []content;
}
