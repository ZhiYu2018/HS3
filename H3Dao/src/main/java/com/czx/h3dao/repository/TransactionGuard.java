package com.czx.h3dao.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
public class TransactionGuard {
    private static TransactionTemplate transactionTemplate = null;

    public TransactionGuard(TransactionTemplate template){
        log.info("TransactionTemplate init: Is null:{}", (template == null));
        if(template != null){
            template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        }
        transactionTemplate = template;
    }

    public static Boolean doTransaction(TransactionCmd cmd){
        Boolean r = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try{
                    cmd.command();
                    return Boolean.TRUE;
                }catch (Throwable t){
                    log.error("doInTransaction exceptions:{}", t.getMessage());
                    transactionStatus.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
        });
        return r;
    }
}
