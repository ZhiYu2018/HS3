package com.czx.h3outbound.ofs;

public class HS3OfsExceptions extends RuntimeException{
    protected HS3OfsExceptions(String msg){
        super(msg);
    }

    public static HS3OfsExceptions of(String msg){
        StringBuilder sb = new StringBuilder();
        sb.append("HS3OfsExceptions--[").append(msg).append("]");
        return new HS3OfsExceptions(sb.toString());
    }
}
