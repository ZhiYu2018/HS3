package com.czx.h3common.security;

import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.google.crypto.tink.mac.MacConfig;
import com.google.crypto.tink.signature.SignatureConfig;

public class TinkRegister {
    private static volatile boolean isDone = false;
    public static void register()throws Exception{
        if(isDone == false) {
            synchronized (TinkRegister.class) {
                AeadConfig.register();
                SignatureConfig.register();
                HybridConfig.register();
                MacConfig.register();
                isDone = true;
            }
        }
    }
}
