package com.czx.h3common;

import com.czx.h3common.git.GitHub;
import com.czx.h3common.git.HS3Github;
import com.czx.h3common.git.dto.*;
import com.czx.h3common.git.vo.FileVo;
import com.czx.h3common.git.vo.TreeMode;
import com.czx.h3common.git.vo.TreeVo;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        FileVo vo = FileVo.builder().path("hello4.txt").owner(owner).contents("Hello world".getBytes())
                .repo(repo).build();
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
        TreeVo vo = TreeVo.builder().mode(TreeMode.SUB_DIR).sha("520541f3b5f86073773fe6f54a37221b6af9772b").owner(owner).path("czx").repo(repo).build();
        TreeDto dto = HS3Github.createDirTree(vo, gitHub);
        System.out.println(gson.toJson(dto));
    }

    @Test
    public void testBlob(){
        BlobDto blobDto = BlobDto.builder().content("Hello, this is blob").encoding("utf-8").build();
        Map<String, Object> map = gitHub.createBlob(owner,repo,blobDto);
        System.out.println(gson.toJson(map));
    }
}
