package com.czx.h3dao.util;

import com.czx.h3dao.po.H3Account;
import com.czx.h3dao.po.H3KeyVault;
import com.czx.h3dao.po.H3OpenIds;
import com.czx.h3outbound.repository.dto.AccountDto;
import com.czx.h3outbound.repository.dto.KeyVaultDto;
import com.czx.h3outbound.repository.dto.OpenIdsDto;

public class Converter {
    /**https://immutables.github.io/immutable.html**/
    public static AccountDto toAccountDto(H3Account h3Account){
        AccountDto accountDto = new AccountDto();
        accountDto.setId(h3Account.getId());
        accountDto.setUid(h3Account.getUid());
        accountDto.setPwd(h3Account.getPwd());
        accountDto.setSalt(h3Account.getSalt());
        accountDto.setGitAccount(h3Account.getGitAccount());
        accountDto.setGitPwd(h3Account.getGitPwd());
        accountDto.setGitOpenFlag(h3Account.getGitOpened());
        return accountDto;
    }

    public static H3Account toH3Account(AccountDto accountDto){
        H3Account account = new H3Account();
        account.setUid(accountDto.getUid());
        account.setPwd(accountDto.getPwd());
        account.setSalt(accountDto.getSalt());
        account.setGitAccount(accountDto.getGitAccount());
        account.setGitPwd(accountDto.getGitPwd());
        account.setGitOpened(accountDto.getGitOpenFlag());
        return account;
    }

    public static KeyVaultDto toKeyVaultDto(H3KeyVault h3KeyVault){
        KeyVaultDto keyVaultDto = new KeyVaultDto();
        keyVaultDto.setKeyName(h3KeyVault.getKeyName());
        keyVaultDto.setKeySalt(h3KeyVault.getKeySalt());
        return keyVaultDto;
    }

    public static H3KeyVault toH3KeyVault(KeyVaultDto dto){
        H3KeyVault vault = new H3KeyVault();
        vault.setKeyName(dto.getKeyName());
        vault.setKeySalt(dto.getKeySalt());
        return vault;
    }

    public static OpenIdsDto toOpenIdsDto(H3OpenIds id){
        OpenIdsDto openIdsDto = new OpenIdsDto();
        openIdsDto.setOpenId(id.getOpenId());
        openIdsDto.setIdType(id.getIdType());
        openIdsDto.setUid(id.getUid());

        return openIdsDto;
    }

    public static H3OpenIds toH3OpenIds(OpenIdsDto dto){
        H3OpenIds h3OpenIds = new H3OpenIds();
        h3OpenIds.setOpenId(dto.getOpenId());
        h3OpenIds.setIdType(dto.getIdType());
        h3OpenIds.setUid(dto.getUid());
        return h3OpenIds;
    }
}
