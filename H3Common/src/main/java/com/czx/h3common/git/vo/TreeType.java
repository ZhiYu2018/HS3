package com.czx.h3common.git.vo;

public enum TreeType {
    BLOB,
    TREE,
    COMMIT;
    public String lowName(){
        return name().toLowerCase();
    }
}
