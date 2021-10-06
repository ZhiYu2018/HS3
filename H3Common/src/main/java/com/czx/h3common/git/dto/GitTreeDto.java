package com.czx.h3common.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitTreeDto {
    private String path;
    private String mode;
    private String type;
    private String sha;
    private String content;
    private String base_tree;
}
