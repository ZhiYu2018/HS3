package com.czx.h3center.domain;

import com.czx.h3dao.repository.TransactionGuard;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.dto.UserRegisterDto;
import com.czx.h3outbound.repository.AccountDaoI;
import com.czx.h3outbound.repository.OpenIdsDaoI;
import com.czx.h3outbound.repository.dto.AccountDto;
import com.czx.h3outbound.repository.dto.ConstantsValue;
import com.czx.h3outbound.repository.dto.OpenIdsDto;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class Account {
    private final AccountDaoI accountDao;
    private final OpenIdsDaoI openIdsDao;
    private final AccountDto accountDto;

    public static Account create(UserRegisterDto dto, AccountDaoI accountDao, OpenIdsDaoI openIdsDao){
        OpenIdsDto openIdsDto = openIdsDao.findBy(dto.getUserId(), dto.getIdType());
        if(openIdsDto != null){
            log.info("User[{},{}] is exist",dto.getUserId(), dto.getIdType());
            ErrorMsg msg = ErrorMsg.builder().code(400).subCode("USER.EXIST").sysServer("H3Center")
                    .msg("Founded").subMsg("Such user is exist").build();
            throw new H3RuntimeException(msg);
        }
        /**采用事务**/
        String uid = createUid();
        log.info("Create uid={} for [{},{}]", uid, dto.getUserId(), dto.getIdType());
        final OpenIdsDto openId= OpenIdsDto.builder().openId(dto.getUserId()).idType(dto.getIdType())
                .uid(uid).build();
        final AccountDto acc = AccountDto.builder().uid(uid).gitOpenFlag(ConstantsValue.ACCOUNT_STATUS_DISABLE)
                .pwd(dto.getPwd()).salt(dto.getKey()).build();

        Boolean r = TransactionGuard.doTransaction(()->{
            openIdsDao.add(openId);
            accountDao.addAccount(acc);
        });
        if(r == Boolean.FALSE){
            log.error("Add user [{},{}] failed", dto.getUserId(), dto.getIdType());
            ErrorMsg msg = ErrorMsg.builder().code(400).subCode("CREATE.USER.FAILED").msg("create failed")
                    .subMsg("").sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }

        return Account.builder().accountDto(acc).accountDao(accountDao).openIdsDao(openIdsDao).build();
    }

    private Account(AccountDaoI accountDao, OpenIdsDaoI openIdsDao, AccountDto accountDto){
        this.accountDao = accountDao;
        this.openIdsDao = openIdsDao;
        this.accountDto = accountDto;
    }


    private static String createUid(){
        char start = 'A';
        StringBuilder sb = new StringBuilder();
        long value = System.nanoTime();
        while (value > 0){
            char v = (char) (start + (int)(value % 26));
            sb.append(v);
            value = value/26;
        }
        return sb.reverse().toString();
    }
}
