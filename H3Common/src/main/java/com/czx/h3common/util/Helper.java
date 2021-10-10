package com.czx.h3common.util;

import com.czx.h3outbound.ofs.HS3OfsExceptions;

public class Helper {
    public static void OfsAssert(boolean cond, String msg){
        if(!cond){
            throw HS3OfsExceptions.of(msg);
        }
    }
}
