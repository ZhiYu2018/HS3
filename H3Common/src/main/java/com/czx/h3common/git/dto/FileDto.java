package com.czx.h3common.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private String message;
    private String content;
    private String sha;
    private String branch;
    private Map<String,Object> committer;
    private Map<String,Object> author;
}
