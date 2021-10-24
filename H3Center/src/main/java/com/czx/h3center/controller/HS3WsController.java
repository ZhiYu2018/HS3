package com.czx.h3center.controller;

import com.czx.h3center.client.HS3StreamClient;
import com.czx.h3facade.Exceptions.ErrorHelper;
import com.czx.h3facade.dto.Request;
import com.czx.h3facade.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ws")
@Slf4j
public class HS3WsController {
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
    public Response<String> sendMsg(@RequestBody Request<String> request){
        Response<String> response = new Response<>();
        log.info("Send ws begin.....");
        streamClient.sendMessage(request.getData());
        ErrorHelper.successResponse(response, "H3Center");
        log.info("Send ws success");
        return response;
    }
}
