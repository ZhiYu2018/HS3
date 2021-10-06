package com.czx.h3common.git.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RepoEntityDto {
    private Long id;
    private String node_id;
    private String name;
    private String full_name;
    private Map<String, Object> owner;
    private Boolean fork;
    private Integer forks_count;
    private Integer forks;
    private Integer stargazers_count;
    private Integer watchers_count;
    private Integer watchers;
    private Integer size;
    private String default_branch;
}
