package com.czx.h3facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HSObject {
    private String uid;
    private String space;
    private String file;
    private Integer number;
    private Boolean isLast;
    private byte []content;
}
