package com.czx.h3common.coder;

import com.alibaba.fastjson.JSON;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;

import static feign.Util.UTF_8;
import static feign.Util.ensureClosed;

public class FastDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.body() == null)
            return null;
        Reader reader = response.body().asReader(UTF_8);
        try{
            InputStream ins = new ReaderInputStream(reader);
            return JSON.parseObject(ins, type);
        }finally {
            ensureClosed(reader);
        }
    }
}
