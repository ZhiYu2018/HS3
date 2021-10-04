package com.czx.h3common.security;

import com.google.crypto.tink.hybrid.HybridConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TinkHybrid {

    public TinkHybrid()throws Exception{
        HybridConfig.register();

    }
}
