package com.czx.h3common.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlobDto {
    private String content;
    private String encoding;
    private String url;
    private String sha;
    private String node_id;
    private Integer size;
}
