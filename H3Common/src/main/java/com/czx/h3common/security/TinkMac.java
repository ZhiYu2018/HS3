package com.czx.h3common.security;

import com.google.crypto.tink.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class TinkMac {
    private final static String KEY_SET_NAME = TinkMac.class.getSimpleName().toLowerCase();
    private KeysetHandle keysetHandle;
    private Mac mac;
    public TinkMac()throws Exception{
        TinkRegister.register();
        keysetHandle = TinkKeyManager.getKeySetHandle(KEY_SET_NAME);
        if(keysetHandle == null) {
            keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("HMAC_SHA256_128BITTAG"));
            TinkKeyManager.StoringKeys(KEY_SET_NAME, keysetHandle);
        }
        mac = keysetHandle.getPrimitive(Mac.class);
    }

    public String computeMac(String data)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] tag = mac.computeMac(data.getBytes(utf8));
        return Base64.getEncoder().encodeToString(tag);
    }

    public boolean verifyMac(String tagText, String data){
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] tag = Base64.getDecoder().decode(tagText.getBytes(utf8));
        byte[] raw = data.getBytes(utf8);
        try {
            mac.verifyMac(tag, raw);
            return Boolean.TRUE;
        }catch (Exception ex){
            log.info("verifyMac {} and {}, exceptions:{}", tagText, data, ex.getMessage());
        }
        return Boolean.FALSE;
    }
}
