package com.czx.h3common.security;

import com.google.crypto.tink.*;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TinkAES {
    private final static String KEY_SET_NAME = TinkAES.class.getSimpleName().toLowerCase();
    private KeysetHandle keysetHandle;
    private Aead aead;
    public TinkAES() throws Exception {
        TinkRegister.register();
        keysetHandle = TinkKeyManager.getKeySetHandle(TinkAES.class.getSimpleName());
        if(keysetHandle == null) {
            keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES128_GCM"));
            TinkKeyManager.StoringKeys(KEY_SET_NAME, keysetHandle);
        }

        aead = keysetHandle.getPrimitive(Aead.class);
    }

    public String encryptII(byte[] rawText, String aad)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] keyBytes = aead.encrypt(rawText, aad.getBytes(utf8));
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public byte []decryptII(String key, String aad)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes());
        byte[] decrypted = aead.decrypt(keyBytes, aad.getBytes(utf8));
        return decrypted;
    }

    public String encrypt(String rawText, String aad) throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] keyBytes = aead.encrypt(rawText.getBytes(utf8), aad.getBytes(utf8));
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public String decrypt(String key, String aad)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes(utf8));
        byte[] decrypted = aead.decrypt(keyBytes, aad.getBytes(utf8));
        return new String(decrypted, utf8);
    }
}
