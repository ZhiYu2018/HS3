package com.czx.h3common.git;

import com.alibaba.fastjson.JSON;
import com.czx.h3common.git.dto.BlobDto;
import com.czx.h3common.git.dto.CommitDto;
import com.czx.h3common.git.dto.RefDto;
import com.czx.h3common.git.dto.TreeDto;
import com.czx.h3common.git.vo.FileContentVo;
import com.czx.h3common.git.vo.GetBlobVo;
import com.czx.h3common.git.vo.TreeMode;
import com.czx.h3common.git.vo.TreeVo;
import com.czx.h3common.security.H3SecurityUtil;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HS3Fs {
    public static void createDir(TreeVo vo, GitHub gitHub){
        log.info("owner:{}, repo:{}", vo.getOwner(), vo.getRepo());
        RefDto refDto = gitHub.getRef(vo.getOwner(), vo.getRepo(), "heads/master");
        log.info("Get ref sha:{}", refDto.getObject().getSha());

        CommitDto commitDto = gitHub.getCommit(vo.getOwner(), vo.getRepo(), refDto.getObject().getSha());
        if(commitDto != null) {
            log.info("Get commit tree.sha:{}", commitDto.getTree().getSha());
            vo.setBase_sha(commitDto.getTree().getSha());
        }

        vo.setPath(vo.getPath() + "/meta.data");
        vo.setMode(TreeMode.FILE_BLOB);
        String meta = "{}";
        vo.setContent(meta);
        TreeDto dto = HS3Github.createTree(vo, gitHub);
        vo.setReturnSha(dto.getSha());
        log.info("Tree={}.sha:{}", vo.getPath(), dto.getSha());


        Map<String,Object> content = new HashMap<>();
        if(commitDto != null) {
            List<String> parent = new ArrayList<>();
            parent.add(commitDto.getSha());
            content.put("parents", parent);
        }

        content.put("tree", dto.getSha());
        content.put("message", "create Path");
        CommitDto newCommit = gitHub.createCommit(vo.getOwner(), vo.getRepo(), content);
        if(newCommit == null){
            throw HS3OfsExceptions.of("Create commit failed");
        }
        content = new HashMap<>();
        log.info("create commit sha:{}", newCommit.getSha());
        content.put("sha", newCommit.getSha());
        content.put("force", Boolean.TRUE);
        refDto = gitHub.updateRef(vo.getOwner(), vo.getRepo(), "heads/master", content);
        log.info("Update ref sha:{}", refDto.getObject().getSha());
    }

    public static void createFile(FileContentVo vo, GitHub gitHub) throws Exception {
        RefDto refDto = gitHub.getRef(vo.getOwner(), vo.getRepo(), "heads/master");
        log.info("Get ref sha:{}", refDto.getObject().getSha());

        CommitDto commitDto = gitHub.getCommit(vo.getOwner(), vo.getRepo(), refDto.getObject().getSha());
        log.info("Get commit tree.sha:{}", commitDto.getTree().getSha());

        TreeVo tVo = TreeVo.builder().build();
        tVo.setBase_sha(commitDto.getTree().getSha());
        tVo.setMode(TreeMode.FILE_BLOB);
        tVo.setPath(getPath(vo.getParent(), vo.getPath()));
        tVo.setOwner(vo.getOwner());
        tVo.setRepo(vo.getRepo());
        tVo.setContent(H3SecurityUtil.AESEncryptFile(vo.getContent(), vo.getSalt()));
        TreeDto dto = HS3Github.createTree(tVo, gitHub);

        Map<String,Object> content = new HashMap<>();
        List<String> parent = new ArrayList<>();
        parent.add(commitDto.getSha());
        content.put("tree", dto.getSha());
        content.put("message", vo.getSalt());
        content.put("parents", parent);
        commitDto = gitHub.createCommit(vo.getOwner(), vo.getRepo(), content);
        log.info("create commit tree.sha:{}", commitDto.getTree().getSha());

        content = new HashMap<>();
        content.put("sha", commitDto.getSha());
        content.put("force", Boolean.TRUE);
        refDto = gitHub.updateRef(vo.getOwner(), vo.getRepo(), "heads/master", content);
        log.info("Update ref sha:{}", refDto.getObject().getSha());
    }

    public static void getBlob(GetBlobVo blobVo, GitHub gitHub) throws Exception {
        log.info("Get blob sha={}", blobVo.getSha());
        BlobDto blobDto = gitHub.getBlob(blobVo.getOwner(), blobVo.getRepo(), blobVo.getSha());
        if(StringUtils.isEmpty(blobDto.getContent())){
            throw new Exception("Content is empty");
        }
        blobVo.setContent(H3SecurityUtil.AESDecryptFile(blobDto.getContent(), blobVo.getSalt()));
    }

    private static String getPath(String parent, String path){
        if(parent == null){
            return path;
        }else{
            return String.format("%s/%s", parent, path);
        }
    }
}
