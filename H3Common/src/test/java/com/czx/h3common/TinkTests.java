package com.czx.h3common;

import com.czx.h3common.security.TinkAES;
import com.czx.h3common.security.TinkDigital;
import com.czx.h3common.security.TinkMac;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class TinkTests{

    @Test
    public void helloWorld(){
        System.out.println("Hello world");
    }


    @Test
    public void testStoringKeys() throws Exception {
        TinkAES tinkAES = new TinkAES();
        tinkAES.StoringKeys();
        String aads[] = {"hahhgajga", "hahghahgaj", "hagajgjajgha"};
        String data = "87vbbojtnfccfvbo;bntfyc x5";
        for(String aad:aads) {
            String key = tinkAES.encrypt(data, aad);
            System.out.println(aad + ",key:" + key);
            String raw = tinkAES.decrypt(key, aad);
            Assert.assertEquals(data, raw);
        }

        TinkMac tinkMac = new TinkMac();
        tinkMac.StoringKeys();
        String tags = tinkMac.computeMac(data);
        System.out.println("tags:" + tags);
        System.out.println("verify:" + tinkMac.verifyMac(tags, data));

        TinkDigital tinkDigital = new TinkDigital();
        tinkDigital.StoringKeys();
        tags = tinkDigital.sign(data);
        System.out.println("tags:" + tags);
        System.out.println("verify:" + tinkDigital.verify(tags, data));
    }
}
