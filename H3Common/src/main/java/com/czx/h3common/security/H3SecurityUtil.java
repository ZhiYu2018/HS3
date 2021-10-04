package com.czx.h3common.security;

import java.util.concurrent.ThreadLocalRandom;

public class H3SecurityUtil {
    public static String getRand(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long value = random.nextLong(0, Long.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        while(value > 0){
            int m = (int)(value % 52);
            if((m >= 26)){
                sb.append((char)('A' + (m - 26)));
            }else{
                sb.append((char)('a' + m));
            }
            value = value / 52;
        }
        return sb.toString();
    }
}
