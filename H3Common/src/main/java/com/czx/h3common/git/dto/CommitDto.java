package com.czx.h3common.git.dto;

import com.czx.h3common.git.vo.TreeRefVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitDto {
    private String sha;
    private String node_id;
    private String url;
    private String html_url;
    private String message;
    private TreeRefVo tree;
    private Map<String,Object> author;
    private Map<String,Object> committer;
    private List<Map<String,Object>> parents;
    private Map<String,Object> verification;
}
