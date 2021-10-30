package com.czx.h3center.controller;

import com.alibaba.fastjson.JSON;
import com.czx.h3center.client.HS3StreamClient;
import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.Exceptions.ErrorMsg;
import com.czx.h3facade.dto.HSObject;
import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ws")
@Slf4j
public class HS3WsController {
    private final static String TEST_DIR = "/opt/hs3/local";
    private HS3StreamClient streamClient;

    @PostMapping("/open")
    @ResponseBody
    public Response<String> open(@RequestBody Request<Map<String,String>> request){
        Response<String> response = new Response<>();
        Map<String,String> map = request.getData();
        streamClient = new HS3StreamClient(map.get("ws.url"));
        Map<String,String> headers = new HashMap<>();
        headers.put("X-HS3-UID", map.get("name"));
        headers.put("X-HS3-TOKEN", map.get("token"));
        headers.put("X-HS3-S-KEY", map.get("sessionKey"));
        streamClient.doHandler(headers);
        ErrorHelper.successResponse(response, "H3Center");
        log.info("Open ws success");
        return response;
    }

    @PostMapping("/send")
    @ResponseBody
    public Response<String> sendMsg(@RequestBody Request<Map<String,String>> request){
        Response<String> response = new Response<>();
        log.info("Send ws begin to send:{}", request.getData());
        Map<String,String> map = request.getData();
        File name = new File(TEST_DIR, map.get("file"));
        HSObject hsObject = HSObject.builder().space(map.get("space"))
                .uid(map.get("uid")).file(map.get("file")).build();
        try(FileInputStream fis = FileUtils.openInputStream(name)){
            byte [] content = new byte[8192];
            int num = 0;
            while (true){
                int l = fis.read(content);
                hsObject.setNumber(num);
                if(l == content.length){
                    hsObject.setIsLast(false);
                    hsObject.setContent(content);
                }else{
                    hsObject.setIsLast(true);
                    hsObject.setContent(Arrays.copyOf(content, l));
                }

                String json = JSON.toJSONString(hsObject);
                streamClient.sendMessage(json, hsObject.getIsLast());
                num++;
                if(hsObject.getIsLast()){
                    break;
                }
            }
            ErrorHelper.successResponse(response, "H3Center");
            log.info("Send ws success");
        }catch (Exception ex){
            ErrorMsg msg = ErrorMsg.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .msg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).subCode("WS.SEND.FAILED")
                    .subMsg(ex.getMessage()).sysServer("HS3Ws").build();
            ErrorHelper.setResponse(response, msg);
            log.warn("Send File={}, exceptions:{}", request.getData(), ex.getMessage());
        }
        return response;
    }
}
