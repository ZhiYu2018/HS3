package com.czx.h3center.domain;

import com.czx.h3common.security.H3SecurityUtil;
import com.czx.h3common.security.HSTink;
import com.czx.h3dao.repository.TransactionGuard;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.Exceptions.H3RuntimeException;
import com.czx.h3facade.dto.ApplyHomeDto;
import com.czx.h3facade.dto.UserLoginDto;
import com.czx.h3facade.dto.UserRegisterDto;
import com.czx.h3facade.dto.UserTokenDto;
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

    public static Account createAccount(UserRegisterDto dto, AccountDaoI accountDao, OpenIdsDaoI openIdsDao){
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
                    .subMsg("create failed").sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }

        return Account.builder().accountDto(acc).accountDao(accountDao).openIdsDao(openIdsDao).build();
    }

    public static Account getAccount(UserTokenDto dto, AccountDaoI accountDao, OpenIdsDaoI openIdsDao){
        AccountDto acc = AccountDto.builder().uid(dto.getName()).build();
        return Account.builder().accountDto(acc).accountDao(accountDao).openIdsDao(openIdsDao).build();
    }

    public static Account findAccount(UserLoginDto dto, AccountDaoI accountDao, OpenIdsDaoI openIdsDao){
        OpenIdsDto openIdsDto = openIdsDao.findBy(dto.getOpenId(), dto.getIdType());
        if(openIdsDto == null){
            log.info("User[{},{}] is not exist",dto.getOpenId(), dto.getIdType());
            ErrorMsg msg = ErrorMsg.builder().code(404).subCode("USER.NOT.EXIST").sysServer("H3Center")
                    .msg("Not Founded").subMsg("None such user").build();
            throw new H3RuntimeException(msg);
        }

        AccountDto acc = accountDao.findByUid(openIdsDto.getUid());
        if(acc == null){
            log.error("User[{}] is not exist", openIdsDto.getUid());
            ErrorMsg msg = ErrorMsg.builder().code(404).subCode("USER.NOT.EXIST").sysServer("H3Center")
                    .msg("Not Founded").subMsg("None such user").build();
            throw new H3RuntimeException(msg);
        }

        return Account.builder().accountDto(acc).accountDao(accountDao).openIdsDao(openIdsDao).build();
    }

    public UserTokenDto createToken(HSTink hsTink){
        UserTokenDto token = new UserTokenDto();
        token.setName(accountDto.getUid());
        try{
            token.setSessionKey(H3SecurityUtil.getRand());
            token.setToken(hsTink.getTinkJwt().sign(token.getName(), token.getSessionKey()));
        }catch (Exception ex){
            log.error("createToken for={},exceptions:{}", accountDto.getUid(), ex.getMessage());
            ErrorMsg msg =  ErrorMsg.builder().code(400).subCode("CREATE.USER.FAILED").msg("create token failed")
                    .subMsg(ex.getMessage()).sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }
        return token;
    }

    public static void verifyToken(UserTokenDto token, HSTink hsTink){
        try{
            boolean verifyR = hsTink.getTinkJwt().verify(token.getToken(), token.getName(), token.getSessionKey());
            if(verifyR == false){
                log.info("verify token: uid={}, token={}, key={} failed", token.getName(), token.getToken(),
                        token.getSessionKey());
                ErrorMsg msg =  ErrorMsg.builder().code(400).subCode("VERIFY.TOKEN.FAILED").msg("TOKEN ERROR")
                        .subMsg("TOKEN ERROR").sysServer("H3Center").build();
                throw new H3RuntimeException(msg);
            }
        }catch (Exception ex){
            if(ex instanceof H3RuntimeException){
                throw ex;
            }
            log.info("verify token: uid={}, token={}, key={} exceptions:{}", token.getName(), token.getToken(),
                    token.getSessionKey(), ex.getMessage());
            ErrorMsg msg =  ErrorMsg.builder().code(400).subCode("VERIFY.TOKEN.FAILED").msg("Exception")
                    .subMsg(ex.getMessage()).sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }
    }

    public void verifyLogin(UserLoginDto dto){
        String hMac = H3SecurityUtil.hMac(accountDto.getPwd(), dto.getSalt());
        if(hMac.compareTo(dto.getKeySalt()) != 0){
            log.info("KeySlat:{}, Salt:{}, hMac:{} is not equal", dto.getKeySalt(), dto.getSalt(), hMac);
            ErrorMsg msg =  ErrorMsg.builder().code(400).subCode("AUTH.USER.FAILED").msg("Auth failed")
                    .subMsg("Auth failed").sysServer("H3Center").build();
            throw new H3RuntimeException(msg);
        }
    }

    public void setGitAccount(ApplyHomeDto dto){
        accountDto.setGitPwd(dto.getGitPwd());
        accountDto.setGitAccount(dto.getGitAccount());
        accountDto.setGitOpenFlag(dto.getGitFlag());
        accountDao.updateAccount(accountDto);
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
