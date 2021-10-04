package com.czx.h3center.domain;

import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.dto.ApplyHomeDto;
import com.czx.h3facade.dto.UserLoginDto;
import com.czx.h3facade.dto.UserRegisterDto;
import com.czx.h3outbound.repository.AccountDaoI;
import com.czx.h3outbound.repository.OpenIdsDaoI;
import com.czx.h3outbound.repository.dto.AccountDto;
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
        try{
            Account account = Account.createAccount(dto, accountDao, openIdsDao);
            return account;
        }catch (Exception ex){
            if(ex instanceof H3RuntimeException){
                throw ex;
            }
            ErrorMsg msg =  ErrorMsg.builder().code(500).subCode("SYSTEM.ERROR").msg("system error")
                    .subMsg(ex.getMessage()).sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }
    }

    public Account findAccount(UserLoginDto dto){
        try{
            Account account = Account.findAccount(dto, accountDao, openIdsDao);
            return account;
        }catch (Exception ex){
            if(ex instanceof H3RuntimeException){
                throw ex;
            }
            ErrorMsg msg =  ErrorMsg.builder().code(500).subCode("SYSTEM.ERROR").msg("system error")
                    .subMsg(ex.getMessage()).sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }
    }

    public Account getAccount(ApplyHomeDto dto){
        Account account = Account.getAccount(dto, accountDao, openIdsDao);
        return account;
    }
}
