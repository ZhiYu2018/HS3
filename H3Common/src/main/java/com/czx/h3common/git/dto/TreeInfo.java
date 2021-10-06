package com.czx.h3common.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeInfo {
    private String path;
    private String mode;
    private String type;
    private Integer size;
    private String sha;
    private String url;
}
