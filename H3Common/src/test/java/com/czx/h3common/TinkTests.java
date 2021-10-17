package com.czx.h3common;

import com.czx.h3common.security.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TinkTests{

    @Test
    public void helloWorld(){
        System.out.println("Hello world");
    }


    @Test
    public void testStoringKeys() throws Exception {
        TinkAES tinkAES = new TinkAES();
        String aads[] = {"hahhgajga", "hahghahgaj", "hagajgjajgha"};
        String data = "87vbbojtnfcchoayugqouajgjaoeqo0-8110!fvbo;bntfyc x5";
        for(String aad:aads) {
            String key = tinkAES.encrypt(data, aad);
            System.out.println(aad + ",key:" + key);
            String raw = tinkAES.decrypt(key, aad);
            Assert.assertEquals(data, raw);
        }

        TinkMac tinkMac = new TinkMac();
        String tags = tinkMac.computeMac(data);
        System.out.println("tags:" + tags);
        System.out.println("verify:" + tinkMac.verifyMac(tags, data));

        TinkDigital tinkDigital = new TinkDigital();
        tags = tinkDigital.sign(data);
        System.out.println("tags:" + tags);
        System.out.println("verify:" + tinkDigital.verify(tags, data));

        TinkHybrid hybrid = new TinkHybrid();
        tags = hybrid.encrypt(data, "czx");
        System.out.println("hybrid tags:" + tags);
        String newData = hybrid.decrypt(tags, "czx");
        System.out.println("hybrid raw:" + newData);
        Assert.assertEquals(data, newData);

        TinkJwt jwt = new TinkJwt(hybrid);
        tags = jwt.sign(data, "czx");
        System.out.println("jwt tags:" + tags);
        System.out.println("jwt verify:" + jwt.verify(tags, data, "czx"));

        tags = H3SecurityUtil.AESEncrypt(data, "czx");
        System.out.println("AESEncrypt tags:" + tags);
        newData = H3SecurityUtil.AESDecrypt(tags, "czx");
        System.out.println("AESDecrypt raw:[" + newData +"]");
        Assert.assertEquals(data, newData);

        System.out.println("hmac:" + H3SecurityUtil.hMac(data, "czx"));
        System.out.println("Salt:" + H3SecurityUtil.getSalt());

        tags = tinkAES.encryptII(data.getBytes(), "czx");
        System.out.println("encryptII tags:" + tags);
        byte [] rawB = tinkAES.decryptII(tags, "czx");
        newData = new String(rawB);
        System.out.println("AESDecrypt raw:" + newData);
        Assert.assertEquals(data, newData);
    }


    @Test
    public void testStream(){
        Map<String, String> map = new HashMap<>();
        map.put("A", "a");
        map.put("B", "b");
        map.put("C", "c");
        map.put("D", "d");
        Stream<String> stream = map.entrySet().stream().map((t) -> t.getKey() + t.getValue());
        List<String> list = stream.collect(Collectors.toList());
        for(String s:list){
            System.out.println("S:" + s);
        }
    }
}
