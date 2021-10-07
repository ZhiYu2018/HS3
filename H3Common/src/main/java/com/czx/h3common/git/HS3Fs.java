package com.czx.h3common.git;

import com.czx.h3common.git.dto.BlobDto;
import com.czx.h3common.git.dto.CommitDto;
import com.czx.h3common.git.dto.RefDto;
import com.czx.h3common.git.dto.TreeDto;
import com.czx.h3common.git.vo.TreeMode;
import com.czx.h3common.git.vo.TreeVo;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class HS3Fs {
    public static void createDir(TreeVo vo, GitHub gitHub){
        RefDto refDto = gitHub.getRef(vo.getOwner(), vo.getRepo(), "heads/master");
        log.info("Get ref sha:{}", refDto.getObject().getSha());

        CommitDto commitDto = gitHub.getCommit(vo.getOwner(), vo.getRepo(), refDto.getObject().getSha());
        log.info("Get commit tree.sha:{}", commitDto.getTree().getSha());

        vo.setBase_sha(commitDto.getTree().getSha());
        vo.setPath(vo.getPath() + "/meta.data");
        vo.setMode(TreeMode.FILE_BLOB);
        String meta = "{}";
        vo.setContent(Base64.getEncoder().encode(meta.getBytes(StandardCharsets.UTF_8)));
        TreeDto dto = HS3Github.createTree(vo, gitHub);
        log.info("Tree.sha:{}", dto.getSha());

        Map<String,Object> content = new HashMap<>();
        List<String> parent = new ArrayList<>();
        parent.add(commitDto.getSha());
        content.put("tree", dto.getSha());
        content.put("message", "create Path");
        content.put("parents", parent);
        commitDto = gitHub.createCommit(vo.getOwner(), vo.getRepo(), content);
        log.info("create commit tree.sha:{}", commitDto.getTree().getSha());

        content = new HashMap<>();
        content.put("sha", commitDto.getSha());
        content.put("force", Boolean.TRUE);
        refDto = gitHub.updateRef(vo.getOwner(), vo.getRepo(), "heads/master", content);
        log.info("Update ref sha:{}", refDto.getObject().getSha());
    }

    public static void createFile(TreeVo vo, GitHub gitHub){
        RefDto refDto = gitHub.getRef(vo.getOwner(), vo.getRepo(), "heads/master");
        log.info("Get ref sha:{}", refDto.getObject().getSha());

        CommitDto commitDto = gitHub.getCommit(vo.getOwner(), vo.getRepo(), refDto.getObject().getSha());
        log.info("Get commit tree.sha:{}", commitDto.getTree().getSha());


        vo.setBase_sha(commitDto.getTree().getSha());
        TreeDto dto = HS3Github.createTree(vo, gitHub);
        log.info("Tree.sha:{}", dto.getSha());

        Map<String,Object> content = new HashMap<>();
        List<String> parent = new ArrayList<>();
        parent.add(commitDto.getSha());
        content.put("tree", dto.getSha());
        content.put("message", "create file:" + vo.getPath());
        content.put("parents", parent);
        commitDto = gitHub.createCommit(vo.getOwner(), vo.getRepo(), content);
        log.info("create commit tree.sha:{}", commitDto.getTree().getSha());

        content = new HashMap<>();
        content.put("sha", commitDto.getSha());
        content.put("force", Boolean.TRUE);
        refDto = gitHub.updateRef(vo.getOwner(), vo.getRepo(), "heads/master", content);
        log.info("Update ref sha:{}", refDto.getObject().getSha());
    }
}
