package com.czx.h3common.coder;

import com.alibaba.fastjson.JSON;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.reflect.Type;

public class FastEncoder implements Encoder {
    @Override
    public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {
        requestTemplate.body(JSON.toJSONString(o));
    }
}
