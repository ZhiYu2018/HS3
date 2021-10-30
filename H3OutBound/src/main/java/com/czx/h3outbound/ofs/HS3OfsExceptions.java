package com.czx.h3outbound.ofs;

import lombok.Data;

@Data
public class HS3OfsExceptions extends RuntimeException{
    private boolean canIgnore;
    protected HS3OfsExceptions(String msg){
        super(msg);
    }

    public static HS3OfsExceptions of(String msg){
        StringBuilder sb = new StringBuilder();
        sb.append("HS3OfsExceptions--[").append(msg).append("]");
        HS3OfsExceptions hs3OfsExceptions = new HS3OfsExceptions(sb.toString());
        hs3OfsExceptions.setCanIgnore(false);
        return hs3OfsExceptions;
    }

    public static HS3OfsExceptions of(String msg, boolean canIgnore){
        StringBuilder sb = new StringBuilder();
        sb.append("HS3OfsExceptions--[").append(msg).append("]");
        HS3OfsExceptions hs3OfsExceptions = new HS3OfsExceptions(sb.toString());
        hs3OfsExceptions.setCanIgnore(canIgnore);
        return hs3OfsExceptions;
    }

}
