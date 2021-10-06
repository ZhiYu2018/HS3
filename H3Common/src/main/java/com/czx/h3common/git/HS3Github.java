package com.czx.h3common.git;

import com.czx.h3common.git.dto.FileDto;
import com.czx.h3common.git.dto.FileMetaDto;
import com.czx.h3common.git.vo.FileVo;
import com.czx.h3common.gson.GsonDecoder;
import com.czx.h3common.gson.GsonEncoder;
import com.czx.h3common.security.H3SecurityUtil;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class HS3Github {
    public static GitHub connect(String user, String token){
        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new TokenAuthRequestInterceptor(token))
                .target(GitHub.class, "https://api.github.com");
        return github;
    }

    static class TokenAuthRequestInterceptor implements RequestInterceptor{
        private String token;
        public TokenAuthRequestInterceptor(String t){
            token = t;
        }

        @Override
        public void apply(RequestTemplate requestTemplate) {
            String ht = String.format("token %s", token);
            requestTemplate.header("Authorization", ht);
        }
    }

    public static FileMetaDto putFile(FileVo fileVo, GitHub gitHub){
        Map<String, Object> committer = new HashMap<>();
        committer.put("name","HS3");
        committer.put("email","hs3@126.com");
        String content = Base64.getEncoder().encodeToString(fileVo.getContents());
        FileDto dto = FileDto.builder().message("Put a file").content(content).committer(committer)
                .sha(fileVo.getSha()).build();
        FileMetaDto map = gitHub.putFile(fileVo.getOwner(), fileVo.getRepo(), fileVo.getPath(), dto);
        return map;
    }

    public static void deleteFile(FileVo fileVo, GitHub gitHub){
        Map<String, Object> committer = new HashMap<>();
        committer.put("name","HS3");
        committer.put("email","hs3@126.com");
        FileDto dto = FileDto.builder().committer(committer).message("Delete file").sha(fileVo.getSha()).build();
        gitHub.deleteFile(fileVo.getOwner(), fileVo.getRepo(), fileVo.getPath(), dto);
    }

}
