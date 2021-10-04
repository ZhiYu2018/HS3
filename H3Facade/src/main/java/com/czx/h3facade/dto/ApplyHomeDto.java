package com.czx.h3facade.dto;

import lombok.Data;

@Data
public class ApplyHomeDto extends UserTokenDto{
    private String gitAccount;
    private String gitPwd;
    private Short gitFlag;
}
