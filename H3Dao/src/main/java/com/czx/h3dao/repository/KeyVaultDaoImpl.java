package com.czx.h3dao.repository;

import com.czx.h3dao.mapper.H3KeyVaultMapper;
import com.czx.h3dao.po.H3KeyVault;
import com.czx.h3dao.po.H3KeyVaultExample;
import com.czx.h3dao.util.Converter;
import com.czx.h3outbound.repository.KeyVaultDaoI;
import com.czx.h3outbound.repository.dto.KeyVaultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class KeyVaultDaoImpl implements KeyVaultDaoI {
    @Autowired
    private H3KeyVaultMapper mapper;
    @Override
    public List<KeyVaultDto> getAll() {
        H3KeyVaultExample example = new H3KeyVaultExample();
        example.setDistinct(true);
        example.setOrderByClause("key_name");
        List<H3KeyVault> keyVaults = mapper.selectByExample(example);
        List<KeyVaultDto> keyVaultDtoList = new ArrayList<>();
        if(keyVaults != null){
            for(H3KeyVault v:keyVaults){
                keyVaultDtoList.add(Converter.toKeyVaultDto(v));
            }
        }
        return keyVaultDtoList;
    }

    @Override
    public int addOrUpdate(KeyVaultDto vaultDto) {
        H3KeyVault vault = Converter.toH3KeyVault(vaultDto);
        try{
            return mapper.insertSelective(vault);
        }catch (DuplicateKeyException ex){
            log.info("Key={} is exist", vault.getKeyName());
            vault.setModifyTime(LocalDateTime.now());
            return mapper.updateByPrimaryKeySelective(vault);
        }
    }

    @Override
    public int delete(String key) {
        return mapper.deleteByPrimaryKey(key);
    }
}
