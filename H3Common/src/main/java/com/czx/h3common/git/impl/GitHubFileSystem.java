package com.czx.h3common.git.impl;

import com.czx.h3common.git.GitHub;
import com.czx.h3common.git.HS3Fs;
import com.czx.h3common.git.HS3Github;
import com.czx.h3common.git.dto.CommitDto;
import com.czx.h3common.git.dto.RefDto;
import com.czx.h3common.git.dto.TreeDto;
import com.czx.h3common.git.impl.vo.HomeMeta;
import com.czx.h3common.git.vo.TreeInfo;
import com.czx.h3common.git.vo.TreeMode;
import com.czx.h3common.git.vo.TreeVo;
import com.czx.h3common.util.LruCache;
import com.czx.h3outbound.ofs.HS3File;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import com.czx.h3outbound.ofs.vo.FileMeta;
import com.czx.h3outbound.ofs.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class GitHubFileSystem implements HS3FileSystem {
    private final static int MAX_TIME_LIVE =  1000*60*5;
    private final static int MAX_SIZE = 100000;
    private UserInfo usi;
    private GitHub gitHub;
    private LruCache<String, HomeMeta> treeMeta;

    public GitHubFileSystem(UserInfo usi){
        this.usi = usi;
        gitHub = HS3Github.connect(usi.getOwner(), usi.getToken());
        treeMeta = new LruCache<>(MAX_SIZE, MAX_TIME_LIVE);
        log.info("Owner={},token=[{}]", usi.getOwner(), usi.getToken());
    }

    @Override
    public void apply(String home) {
        TreeVo vo = TreeVo.builder().owner(usi.getOwner()).repo(usi.getRepo()).path(home)
                .mode(TreeMode.SUB_DIR).build();
        try{
            HS3Fs.createDir(vo, gitHub);
            treeMeta.put(home, HomeMeta.builder().sha(vo.getReturnSha()).subTreeSha(new ConcurrentHashMap<>())
                    .timeLive(System.currentTimeMillis()).build());
        }catch (Throwable t){
            log.warn("Apply home={} exceptions:{}", home, t.getMessage());
            throw HS3OfsExceptions.of(t.getMessage());
        }
    }

    @Override
    public void createDir(String home, String path) {
        if(StringUtils.isEmpty(home) || StringUtils.isEmpty(path)){
            log.info("Home={} or Path={} is null", home, path);
            throw  HS3OfsExceptions.of("Args error");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(home).append("/").append(path);
        TreeVo vo = TreeVo.builder().owner(usi.getOwner()).repo(usi.getRepo()).path(sb.toString())
                .mode(TreeMode.SUB_DIR).build();
        try{
            HS3Fs.createDir(vo, gitHub);
            HomeMeta homeMeta = treeMeta.get(home);
            if(homeMeta == null) {
                homeMeta = HomeMeta.builder().sha(vo.getReturnSha()).subTreeSha(new ConcurrentHashMap<>()).build();
                treeMeta.put(home, homeMeta);
            }
            homeMeta.getSubTreeSha().put(vo.getPath(), vo.getReturnSha());
        }catch (Throwable t){
            log.warn("Apply home={} exceptions:", home, t.getMessage());
            throw HS3OfsExceptions.of(t.getMessage());
        }
    }

    @Override
    public List<FileMeta> list(String home, String path) {
        HomeMeta homeMeta = treeMeta.get(home);
        if(homeMeta == null){
            loadHome();
        }

        if(StringUtils.isEmpty(path)) {
            Stream<FileMeta> stream = homeMeta.getSubTreeSha().entrySet().stream()
                    .map((t) -> FileMeta.builder().uid(home).path(t.getKey()).sha(t.getValue()).build());
            return stream.collect(Collectors.toList());
        }

        String sha = homeMeta.getSubTreeSha().get(path);
        if(StringUtils.isEmpty(sha)){
            log.info("Path={} is not exist, for find none sha");
            return new ArrayList<>();
        }

        try{
            TreeDto treeDto = gitHub.getGitTree(usi.getOwner(), usi.getRepo(), sha, new HashMap<>());
            Stream<FileMeta> stream = treeDto.getTree().stream()
                                      .filter(r-> (r.getPath().equals("meta.data") && r.getType().equals("blob")))
                                      .map((t) -> FileMeta.builder().uid(home).path(t.getPath()).sha(t.getSha()).build());
            return stream.collect(Collectors.toList());
        }catch (Exception ex){
            log.warn("List home={},Space={} exceptions:", home, path, ex.getMessage());
            throw HS3OfsExceptions.of(ex.getMessage());
        }

    }

    @Override
    public HS3File open(FileMeta fileMeta) {
        return null;
    }

    private void loadHome(){
        RefDto refDto = gitHub.getRef(usi.getOwner(), usi.getRepo(), "heads/master");
        CommitDto commitDto = gitHub.getCommit(usi.getOwner(), usi.getRepo(), refDto.getObject().getSha());
        log.info("Get repo:{} last commit:{}", usi.getRepo(), commitDto.getTree());
        Map<String,Object> q = new HashMap<>();
        TreeDto treeDto = gitHub.getGitTree(usi.getOwner(), usi.getRepo(), refDto.getObject().getSha(), q);
        for(TreeInfo ti: treeDto.getTree()) {
            if(!ti.getMode().equals(TreeMode.SUB_DIR.getMode())){
                log.info("Path={},Mode={} is not dir", ti.getPath(), ti.getMode());
                continue;
            }
            log.info("Path+{}, Sha={}", ti.getPath(), ti.getSha());
            HomeMeta homeMeta = treeMeta.get(ti.getPath());
            if(homeMeta == null){
                homeMeta = HomeMeta.builder().sha(ti.getSha()).subTreeSha(new ConcurrentHashMap<>()).build();
                treeMeta.put(ti.getPath(), homeMeta);
                continue;
            }
            homeMeta.setSha(ti.getSha());
        }
    }
}
