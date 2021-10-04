package com.czx.h3common.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TinkJwt {
    private final static String JWT_TYPE = "JWT_ES256";
    private final static long MAX_TIME_SECOND = 8 * 3600;
    private TinkHybrid hybrid;
    public TinkJwt(TinkHybrid hybrid){
        this.hybrid = hybrid;
    }

    public String sign(String subject, String aad)throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append(getSecond(System.currentTimeMillis())).append(".").append(subject);
        return hybrid.encrypt(sb.toString(), aad);
    }

    public boolean verify(String signData, String data, String aad){
        try {
            String newData = hybrid.decrypt(signData, aad);
            int timePos = newData.indexOf(".");
            long current = getSecond(System.currentTimeMillis());
            String nowStr = String.valueOf(current);
            if((timePos < 0) || (timePos > nowStr.length())){
                log.info("Time pos is error:{}", timePos);
                return false;
            }
            Long last = Long.valueOf(newData.substring(0, timePos));
            long diff = (current - last);
            if(diff >= MAX_TIME_SECOND){
                log.info("It is time over:{} - {} = {} > {}", current, last, diff, MAX_TIME_SECOND);
                return false;
            }

            String subject = newData.substring(timePos + 1);
            boolean r = (data.compareTo(subject) == 0);
            if(r == false){
                log.info("verify failed: {} != {}", subject, data);
            }
            return r;
        }catch (Exception ex){

        }
        return false;
    }


    private static long getSecond(long ms){
        return (ms/1000);
    }

}
