package com.czx.h3common.git.dto;

import com.czx.h3common.git.vo.RefObj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefDto {
    private String ref;
    private String node_id;
    private String url;
    private RefObj object;
}
