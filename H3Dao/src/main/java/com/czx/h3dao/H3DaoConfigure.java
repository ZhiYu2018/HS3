package com.czx.h3dao;

import com.czx.h3dao.repository.*;
import com.czx.h3outbound.repository.AccountDaoI;
import com.czx.h3outbound.repository.HomeNasDaoI;
import com.czx.h3outbound.repository.KeyVaultDaoI;
import com.czx.h3outbound.repository.OpenIdsDaoI;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "hs3.dao", value = "enable", havingValue = "true", matchIfMissing = false)
@MapperScan("com.czx.h3dao.mapper")
@Slf4j
public class H3DaoConfigure {
    public H3DaoConfigure(){
        log.info("---------------->>>> H3DaoConfigure init");
    }

    @Bean
    @Primary
    public MybatisProperties getMybatisProperties(){
        log.info("---------------->>>> getMybatisProperties");
        MybatisProperties properties = new MybatisProperties();
        properties.setConfigLocation("classpath:mybatis-config.xml");
        properties.setMapperLocations(new String[]{"classpath:mapper/*.xml"});
        properties.setTypeAliasesPackage("com.czx.h3dao.po");
        return properties;
    }

    @Bean
    public AccountDaoI getAccountDao(){
        return new AccountDaoImpl();
    }

    @Bean
    public KeyVaultDaoI getKeyVaultDao(){
        return new KeyVaultDaoImpl();
    }

    @Bean
    public OpenIdsDaoI getOpenIdsDao(){
        return new OpenIdsDaoImpl();
    }

    @Bean
    public HomeNasDaoI getHomeNasDao(){
        return new HomeNasDaoImpl();
    }

    @Bean
    public TransactionGuard getGuard(TransactionTemplate template){
        return new TransactionGuard(template);
    }
}
