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
public class FileMetaDto {
    private String message;
    private Map<String, Object> content;
    private Map<String, Object> commit;
    private Map<String, Object> committer;
    private Map<String, Object> tree;
    private Map<String, Object> parents;
    private Map<String, Object> verification;
}
