package com.czx.h3common.git.dto;

public enum TreeType {
    BLOB,
    TREE,
    COMMIT;
    public String lowName(){
        return name().toLowerCase();
    }
}
