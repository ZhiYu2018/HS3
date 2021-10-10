package com.czx.h3common;

import com.czx.h3common.git.GitHub;
import com.czx.h3common.git.HS3Fs;
import com.czx.h3common.git.HS3Github;
import com.czx.h3common.git.dto.*;
import com.czx.h3common.git.vo.FileVo;
import com.czx.h3common.git.vo.TreeInfo;
import com.czx.h3common.git.vo.TreeMode;
import com.czx.h3common.git.vo.TreeVo;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GitTests {
    private Gson gson;
    private Properties properties;
    private GitHub gitHub;
    private String owner;
    private String repo;
    public GitTests()throws Exception{
        gson = new Gson();
        File file = new File("/tink/hs3.txt");
        properties = new Properties();
        properties.load(new FileInputStream(file));
        owner = properties.getProperty("login");
        repo  = "hs3_data";
        gitHub = HS3Github.connect(owner, properties.getProperty("oauth"));
    }

    @Test
    public void testGits(){
        RepoQueryDto qry = RepoQueryDto.builder().type("owner").sort("created").direction("asc").build();
        List<RepoEntityDto> list = gitHub.getAuthUserRepos(qry);
        System.out.println("size:" + list.size());
        for(RepoEntityDto d:list){
            System.out.println(d.getId() + "," + d.getName());
        }

        System.out.println(">>>" + gitHub.rateLimit());
        FileVo vo = FileVo.builder().path("hello4.txt").owner(owner).contents("Hello world").repo(repo).build();
        vo.setSha("70c379b63ffa0795fdbfbc128e5a2818397b7ef8");
        FileMetaDto map = HS3Github.putFile(vo, gitHub);
        System.out.println("Sha:" + vo.getSha());
        System.out.println(gson.toJson(map));
        vo.setSha("70c379b63ffa0795fdbfbc128e5a2818397b7ef8");
        HS3Github.deleteFile(vo, gitHub);

        //TreeDto treeDto = gitHub.getGitTree(owner, "hs3_data", null);
        //System.out.println(gson.toJson(treeDto));
    }

    @Test
    public void testGitTree(){
        String content = "Hello czx, this is a file";
        //TreeVo vo = TreeVo.builder().mode(TreeMode.FILE_BLOB).content(content).owner(owner).path("picture/cyr.txt").repo(repo).build();
        //HS3Fs.createFile(vo, gitHub);
    }

    @Test
    public void testCreateDir(){
        TreeVo vo = TreeVo.builder().mode(TreeMode.SUB_DIR).owner(owner).path("picture").repo(repo).build();
        HS3Fs.createDir(vo, gitHub);
    }

    @Test
    public void testCreateCommit(){
        Map<String,Object> content = new HashMap<>();
        content.put("tree", "d3af643ff3460c519ce6ff1a491f010e8b04df9e");
        content.put("message", "commit a tree");
        CommitDto commitDto = gitHub.createCommit(owner, repo, content);
        System.out.println(gson.toJson(commitDto));
    }

    @Test
    public void testBlob(){
        String content = "Hello, this is blob";
        BlobDto blobDto = BlobDto.builder().content(Base64.getEncoder()
                .encodeToString(content.getBytes(StandardCharsets.UTF_8))).encoding("base64").build();
        BlobDto map = gitHub.createBlob(owner,repo,blobDto);
        System.out.println(gson.toJson(map));
    }

    @Test
    public void testGetBlog() {
        String sha = "520541f3b5f86073773fe6f54a37221b6af9772b";
        BlobDto blobDto = gitHub.getBlob(owner, repo, sha);
        String content = blobDto.getContent().trim();
        System.out.println("[" + content + ']');
        System.out.println(new String(Base64.getDecoder().decode(content), StandardCharsets.UTF_8));

        String ct = "VTBkV2MySkhPR2RaTTNBMFRFTkNNR0ZIYkhwSlIyeDZTVWRGWjFwdGJITmFVVDA5";
        System.out.println(new String(Base64.getDecoder().decode(ct), StandardCharsets.UTF_8));
    }

    @Test
    public void testGetRef(){
        RefDto refDto = gitHub.getRef(owner, repo, "heads/master");
        System.out.println(gson.toJson(refDto));
        CommitDto commitDto = gitHub.getCommit(owner, repo, refDto.getObject().getSha());
        System.out.println(gson.toJson(commitDto));
    }

    @Test
    public void testUpdateRef(){
        Map<String,Object> content = new HashMap<>();
        content.put("sha", "0c1be45a07d43c259a0b23294dcc892c79ce90ab");
        content.put("force", Boolean.TRUE);
        RefDto refDto = gitHub.updateRef(owner, repo, "heads/master", content);
        System.out.println(gson.toJson(refDto));
    }

    @Test
    public void testGetTree(){
        RefDto refDto = gitHub.getRef(owner, repo, "heads/master");
        Map<String,Object> q = new HashMap<>();
        TreeDto treeDto = gitHub.getGitTree(owner, repo, "48454dceab6dfa6be1d19bc95a951e7137a56f2a", q);
        for(TreeInfo ti: treeDto.getTree()) {
            System.out.println(">>" + gson.toJson(ti));
        }
    }
}
