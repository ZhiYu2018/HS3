package com.czx.h3common.security;

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class TinkAES {
    private KeysetHandle keysetHandle;
    private Aead aead;
    public TinkAES() throws Exception {
        AeadConfig.register();
        keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES128_GCM"));
        aead = keysetHandle.getPrimitive(Aead.class);
    }

    public void StoringKeys() throws Exception {
        String keysetFilename = "aes_keyset.json";
        CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File(keysetFilename)));
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
