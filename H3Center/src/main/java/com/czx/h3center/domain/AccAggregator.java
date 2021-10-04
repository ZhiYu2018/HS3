package com.czx.h3center.domain;

import com.czx.h3facade.dto.UserRegisterDto;
import com.czx.h3outbound.repository.AccountDaoI;
import com.czx.h3outbound.repository.OpenIdsDaoI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccAggregator {
    @Autowired
    private AccountDaoI accountDao;
    @Autowired
    private OpenIdsDaoI openIdsDao;

    public Account createAccount(UserRegisterDto dto){
        Account account = Account.create(dto, accountDao, openIdsDao);
        return account;
    }
}
