package com.czx.h3common.git.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoDto {
    private String name;
    private String description;
    private String homepage;
    private Integer team_id;
    private String gitignore_template;
    private String license_template;

    @SerializedName("private")
    @JSONField(name="private")
    private Boolean isPrivate;
    private Boolean has_wiki;
    private Boolean auto_init;
}
