package com.czx.h3common.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class HSTink {
    private TinkAES tinkAES;
    private TinkHybrid tinkHybrid;
    private TinkDigital tinkDigital;
    private TinkMac tinkMac;
    private TinkJwt tinkJwt;
    public HSTink(){
        try{
            tinkAES = new TinkAES();
            tinkHybrid = new TinkHybrid();
            tinkDigital = new TinkDigital();
            tinkMac = new TinkMac();
            tinkJwt = new TinkJwt(tinkHybrid);
            H3SecurityUtil.setHsTink(this);
        }catch (Exception ex){
            log.error("HSTink exceptions:{}", ex.getMessage());
        }
    }
}
