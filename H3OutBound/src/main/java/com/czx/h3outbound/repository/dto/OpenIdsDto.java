package com.czx.h3outbound.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenIdsDto {
    private String openId;
    private Short idType;
    private String uid;
}
