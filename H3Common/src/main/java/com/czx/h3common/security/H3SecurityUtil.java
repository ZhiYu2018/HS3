package com.czx.h3common.security;

import com.czx.h3common.security.vo.SaltVo;
import com.czx.h3common.util.Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class H3SecurityUtil {
    private static volatile HSTink hsTink;
    public static void setHsTink(HSTink tink){
        hsTink = tink;
    }

    public static HSTink getHsTink(){
        return hsTink;
    }

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

    public static String AESEncryptFile(byte []input, String aad)throws Exception{
        byte [] aadBuf = Base64.getDecoder().decode(aad);
        Helper.OfsAssert((aadBuf.length == 48), "aad len is error");
        byte [] bKey = new byte[16];
        byte [] bIv = new byte[32];
        copyTo(aadBuf, bKey, 0, 0, bKey.length);
        copyTo(aadBuf, bIv, 0 + bKey.length, 0, bIv.length);
        return _AESEncrypt(bKey, bIv, input);
    }

    public static byte [] AESDecryptFile(String data, String aad)throws Exception{
        byte [] aadBuf = Base64.getDecoder().decode(aad);
        Helper.OfsAssert((aadBuf.length == 48), "aad len is error");
        byte [] bKey = new byte[16];
        byte [] bIv = new byte[32];
        return _AESDecrypt(bKey, bIv, data);
    }

    public static String AESEncrypt(String data, String aad)throws Exception{
        byte[] aadKey = getAESKey(aad);
        byte[] input = getUTF8Bytes(data);
        return _AESEncrypt(aadKey, aadKey, input);
    }

    private static String _AESEncrypt(byte [] bKey, byte [] bIv, byte []input)throws Exception{
        SecretKeySpec key = new SecretKeySpec(bKey,"AES");
        IvParameterSpec iv = new IvParameterSpec(bIv);
        Properties properties = new Properties();
        String transform = "AES/CBC/PKCS5Padding";
        try(CryptoCipher encipher = Utils.getCipherInstance(transform, properties)) {
            byte[] output = new byte[input.length * 5];
            encipher.init(Cipher.ENCRYPT_MODE, key, iv);
            int updateBytes = encipher.update(input, 0, input.length, output, 0);
            int finalBytes = encipher.doFinal(input, 0, 0, output, updateBytes);
            byte[] keyBytes = Arrays.copyOf(output, updateBytes+finalBytes);
            return Base64.getEncoder().encodeToString(keyBytes);
        }catch (Exception ex){
            log.info("_AESEncrypt exceptions:{}", ex.getMessage());
            throw ex;
        }
    }

    public static String AESDecrypt(String data, String aad)throws Exception{
        byte[] aadKey = getAESKey(aad);
        byte [] decoded = _AESDecrypt(aadKey, aadKey, data);
        return new String(decoded);
    }

    private static byte[] _AESDecrypt(byte [] bKey, byte [] bIv, String data)throws Exception{
        SecretKeySpec key = new SecretKeySpec(bKey,"AES");
        IvParameterSpec iv = new IvParameterSpec(bIv);
        Properties properties = new Properties();
        String transform = "AES/CBC/PKCS5Padding";
        properties.setProperty(CryptoCipherFactory.CLASSES_KEY, CryptoCipherFactory.CipherProvider.JCE.getClassName());
        try(CryptoCipher decipher = Utils.getCipherInstance(transform, properties)){
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte [] keyBytes = Base64.getDecoder().decode(getUTF8Bytes(data));
            byte [] decoded = new byte[keyBytes.length];
            int len = decipher.doFinal(keyBytes, 0, keyBytes.length, decoded, 0);
            return Arrays.copyOf(decoded, len);
        }catch (Exception ex){
            log.info("AESDecrypt data={},exceptions:{}", data, ex.getMessage());
            throw ex;
        }
    }

    public static String hMac(String data, String aad){
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, getUTF8Bytes(aad));
        byte [] dig = mac.doFinal(getUTF8Bytes(data));
        return Base64.getEncoder().encodeToString(dig);
    }

    public static String sha1(String content){
        MessageDigest digest = DigestUtils.getSha1Digest();
        byte [] dig = digest.digest(content.getBytes());
        return Hex.encodeHexString(dig);
    }

    public static SaltVo getSaltVo(){
        SaltVo saltVo = new SaltVo();
        Properties properties = new Properties();
        properties.put(CryptoRandomFactory.DEVICE_FILE_PATH_DEFAULT,CryptoRandomFactory.RandomProvider.OPENSSL.getClassName());
        try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(properties)) {
            random.nextBytes(saltVo.getKey());
            random.nextBytes(saltVo.getIv());
            return saltVo;
        }catch (Exception ex){
            log.info("getSalt exceptions:{}", ex.getMessage());
            return null;
        }
    }

    public static String getSalt(){
        SaltVo saltVo = getSaltVo();
        byte[] salt = new byte[48];
        copyTo(saltVo.getKey(), salt, 0, 0, saltVo.getKey().length);
        copyTo(saltVo.getIv(), salt, 0, saltVo.getKey().length, saltVo.getIv().length);
        return Base64.getEncoder().encodeToString(salt);
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
