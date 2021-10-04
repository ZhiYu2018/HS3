package com.czx.h3outbound.repository;

import com.czx.h3outbound.repository.dto.AccountDto;

public interface AccountDaoI {
    AccountDto findByUid(String uid);
    int addAccount(AccountDto accountDto);
    int updateAccount(AccountDto accountDto);
    int deleteAccountByUid(String uid);
}
