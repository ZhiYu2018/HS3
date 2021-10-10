package com.czx.h3outbound.ofs.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String owner;
    private String repo;
    private String token;
    private StorageType type;
}
