package com.czx.h3common.git.impl.vo;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentMap;

@Data
@Builder
public class HomeMeta {
    private String sha;
    private long timeLive;
    private ConcurrentMap<String,String> subTreeSha;
}
