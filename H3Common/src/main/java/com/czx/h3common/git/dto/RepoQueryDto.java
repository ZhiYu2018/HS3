package com.czx.h3common.git.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoQueryDto {
    private String visibility;
    private String affiliation;
    private String type;
    private String sort;
    private String direction;
    private Integer per_page;
    private Integer page;
    private String since;
    private String before;
}
