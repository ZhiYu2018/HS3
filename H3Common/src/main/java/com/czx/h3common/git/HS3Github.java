package com.czx.h3common.git;

import com.czx.h3common.git.dto.*;
import com.czx.h3common.git.vo.FileVo;
import com.czx.h3common.git.vo.TreeType;
import com.czx.h3common.git.vo.TreeVo;
import com.czx.h3common.gson.GsonDecoder;
import com.czx.h3common.gson.GsonEncoder;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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

    public static TreeDto createDirTree(TreeVo vo, GitHub gitHub){
        GitTreeDto dto = GitTreeDto.builder().mode(vo.getMode().getMode()).path(vo.getPath()).build();
        switch (vo.getMode()){
            case FILE_BLOB:
            case EXE_BLOB:{
                dto.setContent(Base64.getEncoder().encodeToString(vo.getContent()));
                dto.setType(TreeType.BLOB.lowName());
                break;
            }
            case SUB_DIR:{
                dto.setSha(vo.getSha());
                dto.setType(TreeType.TREE.lowName());
            }
            case SUB_MODULE:{
                dto.setType(TreeType.COMMIT.lowName());
            }
            case SYMLINK:{
                dto.setType(TreeType.BLOB.lowName());
            }
        }

        List<GitTreeDto> treeDtoList = new ArrayList<>();
        treeDtoList.add(dto);
        CreateTreeDto treeDto = CreateTreeDto.builder().tree(treeDtoList).build();
        return gitHub.createTree(vo.getOwner(), vo.getRepo(), treeDto);
    }

}
