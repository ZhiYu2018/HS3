package com.czx.h3common.git;

import com.czx.h3common.coder.*;
import com.czx.h3common.git.dto.*;
import com.czx.h3common.git.vo.FileVo;
import com.czx.h3common.git.vo.TreeType;
import com.czx.h3common.git.vo.TreeVo;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        FileDto dto = FileDto.builder().message("Put a file").content(fileVo.getContents()).committer(committer)
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

    public static TreeDto createTree(TreeVo vo, GitHub gitHub){
        GitTreeDto dto = GitTreeDto.builder().mode(vo.getMode().getMode()).path(vo.getPath()).build();
        dto.setType(TreeType.BLOB.lowName());
        dto.setContent(vo.getContent());
        List<GitTreeDto> treeDtoList = new ArrayList<>();
        treeDtoList.add(dto);
        CreateTreeDto treeDto = CreateTreeDto.builder().base_tree(vo.getBase_sha()).tree(treeDtoList).build();
        return gitHub.createTree(vo.getOwner(), vo.getRepo(), treeDto);
    }

    public static TreeDto createDir(TreeVo vo, GitHub gitHub){
        GitTreeDto dto = GitTreeDto.builder().mode(vo.getMode().getMode()).path(vo.getPath()).build();
        dto.setSha(vo.getContent());
        dto.setType(TreeType.TREE.lowName());
        List<GitTreeDto> treeDtoList = new ArrayList<>();
        treeDtoList.add(dto);
        CreateTreeDto treeDto = CreateTreeDto.builder().base_tree(vo.getBase_sha()).tree(treeDtoList).build();
        return gitHub.createTree(vo.getOwner(), vo.getRepo(), treeDto);
    }

}
