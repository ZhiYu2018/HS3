package com.czx.h3common.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeDto {
    private String sha;
    private String url;
    private Boolean truncated;
    private List<TreeInfo> tree;
}
