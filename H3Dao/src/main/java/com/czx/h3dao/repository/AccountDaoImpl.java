package com.czx.h3dao.repository;

import com.czx.h3dao.mapper.H3AccountMapper;
import com.czx.h3dao.po.H3Account;
import com.czx.h3dao.po.H3AccountExample;
import com.czx.h3dao.util.Converter;
import com.czx.h3outbound.repository.AccountDaoI;
import com.czx.h3outbound.repository.dto.AccountDto;
import com.czx.h3outbound.repository.dto.ConstantsValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class AccountDaoImpl implements AccountDaoI {
    @Autowired
    private H3AccountMapper mapper;

    @Override
    public AccountDto findByUid(String uid) {
        H3AccountExample example = new H3AccountExample();
        example.createCriteria().andUidEqualTo(uid).andStatusEqualTo(ConstantsValue.ACCOUNT_STATUS_ENABLE);

        List<H3Account> accountList = mapper.selectByExample(example);
        if((accountList == null) || accountList.isEmpty()){
            log.warn("Find none such uid={}", uid);
            return null;
        }
        return Converter.toAccountDto(accountList.get(0));
    }

    @Override
    public int addAccount(AccountDto accountDto) {
        H3Account h3Account = Converter.toH3Account(accountDto);
        h3Account.setStatus(ConstantsValue.ACCOUNT_STATUS_ENABLE);
        h3Account.setSysVer((int)System.currentTimeMillis()/1000);
        try{
            int value = mapper.insertSelective(h3Account);
            return value;
        }catch (DuplicateKeyException ex){
            log.info("uid={} is exist", h3Account.getUid());
            H3AccountExample example = new H3AccountExample();
            example.createCriteria().andUidEqualTo(h3Account.getUid());
            return mapper.updateByExampleSelective(h3Account, example);
        }
    }

    @Override
    public int updateAccount(AccountDto accountDto) {
        H3Account h3Account = Converter.toH3Account(accountDto);
        h3Account.setModifyTime(LocalDateTime.now());
        H3AccountExample example = new H3AccountExample();
        example.createCriteria().andUidEqualTo(h3Account.getUid());
        return mapper.updateByExampleSelective(h3Account, example);
    }

    @Override
    public int deleteAccountByUid(String uid) {
        H3Account h3Account = new H3Account();
        h3Account.setStatus(ConstantsValue.ACCOUNT_STATUS_DISABLE);
        H3AccountExample example = new H3AccountExample();
        example.createCriteria().andUidEqualTo(h3Account.getUid());
        return mapper.updateByExampleSelective(h3Account, example);
    }
}
