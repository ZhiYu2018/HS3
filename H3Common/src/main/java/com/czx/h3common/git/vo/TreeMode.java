package com.czx.h3common.git.vo;

public enum TreeMode {
    FILE_BLOB("100644"),
    EXE_BLOB("100755"),
    SUB_DIR("040000"),
    SUB_MODULE("160000"),
    SYMLINK("120000");
    TreeMode(String mode){
        this.mode = mode;
    }
    private String mode;

    public String getMode() {
        return mode;
    }
}
