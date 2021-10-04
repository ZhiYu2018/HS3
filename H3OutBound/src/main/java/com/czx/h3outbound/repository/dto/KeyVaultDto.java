package com.czx.h3outbound.repository.dto;

import lombok.Data;

@Data
public class KeyVaultDto {
    private String keyName;
    private String keySalt;
}
