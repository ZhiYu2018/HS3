package com.czx.h3outbound.repository;

import com.czx.h3outbound.repository.dto.KeyVaultDto;

import java.util.List;

public interface KeyVaultDaoI {
    List<KeyVaultDto> getAll();
    int addOrUpdate(KeyVaultDto vaultDto);
    int delete(String key);
}
