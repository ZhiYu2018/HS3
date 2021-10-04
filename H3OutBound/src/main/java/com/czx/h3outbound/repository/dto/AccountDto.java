package com.czx.h3outbound.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long Id;
    private String uid;
    private String pwd;
    private String salt;
    private String gitAccount;
    private String gitPwd;
    private Short gitOpenFlag;
}
