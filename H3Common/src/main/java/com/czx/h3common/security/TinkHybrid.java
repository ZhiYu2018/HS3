package com.czx.h3common.security;

import com.google.crypto.tink.*;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TinkHybrid {
    private KeysetHandle privateKeysetHandle;
    private KeysetHandle publicKeysetHandle;
    public TinkHybrid()throws Exception{
        TinkRegister.register();
        privateKeysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECIES_P256_COMPRESSED_HKDF_HMAC_SHA256_AES128_GCM"));
        publicKeysetHandle =  privateKeysetHandle.getPublicKeysetHandle();

    }
    public void StoringKeys() throws Exception{
        String keysetFilename = "hybrid_keyset.json";
        CleartextKeysetHandle.write(privateKeysetHandle, JsonKeysetWriter.withFile(new File(keysetFilename)));
    }


    public String encrypt(String rawText, String aad) throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        HybridEncrypt hybridEncrypt = publicKeysetHandle.getPrimitive(HybridEncrypt.class);
        byte[] keyData = hybridEncrypt.encrypt(rawText.getBytes(utf8), aad.getBytes(utf8));
        return Base64.getEncoder().encodeToString(keyData);
    }

    public String decrypt(String key, String aad)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] keyBytes = Base64.getDecoder().decode(key.getBytes(utf8));
        HybridDecrypt hybridDecrypt = privateKeysetHandle.getPrimitive(HybridDecrypt.class);
        byte[] rawBytes = hybridDecrypt.decrypt(keyBytes, aad.getBytes(utf8));
        return new String(rawBytes, utf8);
    }
}
