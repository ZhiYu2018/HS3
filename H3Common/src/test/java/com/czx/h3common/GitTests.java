package com.czx.h3common;

import com.czx.h3common.git.GitHub;
import com.czx.h3common.git.HS3Github;
import com.czx.h3common.git.dto.FileMetaDto;
import com.czx.h3common.git.dto.RepoEntityDto;
import com.czx.h3common.git.dto.RepoQueryDto;
import com.czx.h3common.git.vo.FileVo;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class GitTests {

    @Test
    public void testGits() throws Exception {
        Gson gson = new Gson();
        File file = new File("/tink/hs3.txt");
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        String owner = properties.getProperty("login");
        GitHub gitHub = HS3Github.connect(owner, properties.getProperty("oauth"));
        RepoQueryDto qry = RepoQueryDto.builder().type("owner").sort("created").direction("asc").build();
        List<RepoEntityDto> list = gitHub.getAuthUserRepos(qry);
        System.out.println("size:" + list.size());
        for(RepoEntityDto d:list){
            System.out.println(d.getId() + "," + d.getName());
        }

        System.out.println(">>>" + gitHub.rateLimit());
        FileVo vo = FileVo.builder().path("hello4.txt").owner(owner).contents("Hello world".getBytes())
                .repo("hs3_data").build();
        vo.setSha("70c379b63ffa0795fdbfbc128e5a2818397b7ef8");
        FileMetaDto map = HS3Github.putFile(vo, gitHub);
        System.out.println("Sha:" + vo.getSha());
        System.out.println(gson.toJson(map));
        vo.setSha("70c379b63ffa0795fdbfbc128e5a2818397b7ef8");
        HS3Github.deleteFile(vo, gitHub);

        //TreeDto treeDto = gitHub.getGitTree(owner, "hs3_data", null);
        //System.out.println(gson.toJson(treeDto));
    }
}
