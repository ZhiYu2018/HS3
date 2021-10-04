package com.czx.h3common.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;
import org.apache.commons.crypto.utils.Utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class H3SecurityUtil {
    public static String getRand(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long value = random.nextLong(0, Long.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        while(value > 0){
            int m = (int)(value % 52);
            if((m >= 26)){
                sb.append((char)('A' + (m - 26)));
            }else{
                sb.append((char)('a' + m));
            }
            value = value / 52;
        }
        return sb.toString();
    }

    public static String AESEncrypt(String data, String aad)throws Exception{
        byte[] aadKey = getAESKey(aad);
        SecretKeySpec key = new SecretKeySpec(aadKey,"AES");
        IvParameterSpec iv = new IvParameterSpec(aadKey);
        Properties properties = new Properties();
        String transform = "AES/CBC/PKCS5Padding";
        try(CryptoCipher encipher = Utils.getCipherInstance(transform, properties)) {
            byte[] input = getUTF8Bytes(data);
            byte[] output = new byte[input.length * 5];
            encipher.init(Cipher.ENCRYPT_MODE, key, iv);
            int updateBytes = encipher.update(input, 0, input.length, output, 0);
            int finalBytes = encipher.doFinal(input, 0, 0, output, updateBytes);
            byte[] keyBytes = Arrays.copyOf(output, updateBytes+finalBytes);
            return Base64.getEncoder().encodeToString(keyBytes);
        }catch (Exception ex){
            log.info("AESEncrypt data={},aad={},exceptions:{}", data, aad, ex.getMessage());
            throw ex;
        }
    }

    public static String AESDecrypt(String data, String aad)throws Exception{
        byte[] aadKey = getAESKey(aad);
        SecretKeySpec key = new SecretKeySpec(aadKey,"AES");
        IvParameterSpec iv = new IvParameterSpec(aadKey);
        Properties properties = new Properties();
        String transform = "AES/CBC/PKCS5Padding";
        properties.setProperty(CryptoCipherFactory.CLASSES_KEY, CryptoCipherFactory.CipherProvider.JCE.getClassName());
        try(CryptoCipher decipher = Utils.getCipherInstance(transform, properties)){
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte [] keyBytes = Base64.getDecoder().decode(getUTF8Bytes(data));
            byte [] decoded = new byte[keyBytes.length];
            int len = decipher.doFinal(keyBytes, 0, keyBytes.length, decoded, 0);
            return new String(decoded, 0, len);
        }catch (Exception ex){
            log.info("AESDecrypt data={},aad={},exceptions:{}", data, aad, ex.getMessage());
            throw ex;
        }
    }

    public static String hMac(String data, String aad){
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, getUTF8Bytes(aad));
        byte [] dig = mac.doFinal(getUTF8Bytes(data));
        return Base64.getEncoder().encodeToString(dig);
    }

    public static String getSalt(){
        byte[] key = new byte[16];
        byte[] iv = new byte[32];
        Properties properties = new Properties();
        properties.put(CryptoRandomFactory.DEVICE_FILE_PATH_DEFAULT,
                CryptoRandomFactory.RandomProvider.OPENSSL.getClassName());
        try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(properties)) {
            random.nextBytes(key);
            random.nextBytes(iv);
            byte[] salt = new byte[48];
            copyTo(key, salt, 0, 0, key.length);
            copyTo(iv, salt, 0, key.length, iv.length);
            return Base64.getEncoder().encodeToString(salt);
        }catch (Exception ex){
            log.info("getSalt exceptions:{}", ex.getMessage());
            return null;
        }
    }

    private static void copyTo(byte [] src, byte [] tgt, int o, int n, int len){
        for(int i = 0; i < len; i++){
            tgt[n + i] = src[o + i];
        }
    }

    private static byte[] getAESKey(String aad){
        final int keyLen = 16;
        byte[] bytes = getUTF8Bytes(aad);
        if(bytes.length == keyLen){
            return bytes;
        }
        if(bytes.length > keyLen){
            return Arrays.copyOf(bytes, keyLen);
        }
        byte[] newBytes = new byte[keyLen];
        int i = 0;
        for(; i < bytes.length; i++){
            newBytes[i] = bytes[i];
        }
        for(; i < newBytes.length; i++){
            newBytes[i] = (byte) (i);
        }
        return newBytes;
    }

    private static byte[] getUTF8Bytes(String input){
        return input.getBytes(StandardCharsets.UTF_8);
    }
}
