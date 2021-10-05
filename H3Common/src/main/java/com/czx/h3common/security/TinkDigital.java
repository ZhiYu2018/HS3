package com.czx.h3common.security;

import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class TinkDigital {
    private final static String KEY_SET_NAME = TinkDigital.class.getSimpleName().toLowerCase();
    private KeysetHandle privateKeysetHandle;
    private PublicKeySign signer;
    private KeysetHandle publicKeysetHandle;
    private PublicKeyVerify verifier;

    public TinkDigital() throws Exception{
        TinkRegister.register();
        privateKeysetHandle = TinkKeyManager.getKeySetHandle(KEY_SET_NAME);
        if(privateKeysetHandle == null){
            privateKeysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECDSA_P256"));
            TinkKeyManager.StoringKeys(KEY_SET_NAME, privateKeysetHandle);
        }

        signer = privateKeysetHandle.getPrimitive(PublicKeySign.class);
        publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();
        verifier = publicKeysetHandle.getPrimitive(PublicKeyVerify.class);
    }

    public String sign(String data)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte[] signature = signer.sign(data.getBytes(utf8));
        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verify(String dig, String data)throws Exception{
        Charset utf8 = StandardCharsets.UTF_8;
        byte [] sigBytes = Base64.getDecoder().decode(dig.getBytes(utf8));
        try {
            verifier.verify(sigBytes, data.getBytes(utf8));
            return Boolean.TRUE;
        }catch (Exception ex){
            log.info("verifyMac {} and {}, exceptions:{}", dig, data, ex.getMessage());
        }
        return Boolean.FALSE;
    }
}
