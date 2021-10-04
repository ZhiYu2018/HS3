package com.czx.h3common.security;

import com.google.crypto.tink.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class TinkDigital {
    private KeysetHandle privateKeysetHandle;
    private PublicKeySign signer;
    private KeysetHandle publicKeysetHandle;
    private PublicKeyVerify verifier;

    public TinkDigital() throws Exception{
        TinkRegister.register();
        privateKeysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECDSA_P256"));
        signer = privateKeysetHandle.getPrimitive(PublicKeySign.class);
        publicKeysetHandle = privateKeysetHandle.getPublicKeysetHandle();
        verifier = publicKeysetHandle.getPrimitive(PublicKeyVerify.class);
    }

    public void StoringKeys() throws Exception {
        String keysetFilename = "digital_keyset.json";
        CleartextKeysetHandle.write(privateKeysetHandle, JsonKeysetWriter.withFile(new File(keysetFilename)));
        CleartextKeysetHandle.write(publicKeysetHandle, JsonKeysetWriter.withFile(new File("digital_p_keyset.json")));
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
