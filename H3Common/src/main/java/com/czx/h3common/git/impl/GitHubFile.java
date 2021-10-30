package com.czx.h3common.git.impl;

import com.czx.h3common.git.GitHub;
import com.czx.h3common.git.HS3Fs;
import com.czx.h3common.git.dto.TreeDto;
import com.czx.h3common.git.vo.FileContentVo;
import com.czx.h3common.git.vo.GetBlobVo;
import com.czx.h3common.git.vo.TreeInfo;
import com.czx.h3common.util.Helper;
import com.czx.h3outbound.ofs.HS3File;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import com.czx.h3outbound.ofs.vo.FileMeta;
import com.czx.h3outbound.ofs.vo.FileSegment;
import com.czx.h3outbound.ofs.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GitHubFile implements HS3File {
    private final static int MAX_SIZE_KB = 64*1024;
    private String path;
    private UserInfo usi;
    private GitHub gitHub;
    private FileMeta spaceMeta;
    private ByteBuffer segBuffer;
    private int offset;
    private List<TreeInfo> infoList;

    public void setUsi(UserInfo usi) {
        this.usi = usi;
    }

    public void setGitHub(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    public void setSpaceMeta(FileMeta spaceMeta) {
        this.spaceMeta = spaceMeta;
    }

    @Override
    public void open(String file) {
        this.path = file;
        offset = 0;
        try{
            TreeDto treeDto = gitHub.getGitTree(usi.getOwner(), usi.getRepo(), spaceMeta.getSha(), new HashMap<>());
            Stream<TreeInfo> stream = treeDto.getTree().stream().filter(r->{
                log.info("Path={},Sha={}, targetPath={}", r.getPath(), r.getSha(), path);
                return r.getPath().startsWith(path);
            }).sorted((f, s) ->(f.getPath().compareTo(s.getPath())>= 0? 1:-1));
            infoList = stream.collect(Collectors.toList());
        }catch (Exception ex){
            log.warn("Open path={} exceptions:{}", path, ex.getMessage());
            throw HS3OfsExceptions.of(ex.getMessage());
        }
    }

    @Override
    public FileMeta meta() {
        AtomicInteger num = new AtomicInteger(1);
        FileMeta meta = FileMeta.builder().uid(spaceMeta.getUid()).path(path).build();
        Stream<FileSegment> stream = infoList.stream().map(r-> FileSegment.builder().name(r.getPath()).sha(r.getSha()).num(num.getAndIncrement()).build());
        meta.setSegments(stream.collect(Collectors.toList()));
        return meta;
    }

    @Override
    public int read(ByteBuffer buffer, int len) {
        if(infoList.isEmpty()){
            throw HS3OfsExceptions.of("File is new");
        }

        buffer.clear();
        while(buffer.hasRemaining()){
            if((segBuffer == null) || !segBuffer.hasRemaining()){
                if(readSegment() == 0){
                    break;
                }
            }

            int rl = buffer.remaining();
            if(rl > segBuffer.remaining()){
                rl = segBuffer.remaining();
            }
            buffer.put(segBuffer.array(), segBuffer.position(), rl);
            segBuffer.position(segBuffer.position() + rl);
        }
        int p = buffer.position();
        buffer.flip();
        return p;
    }

    @Override
    public int write(ByteBuffer buffer, int len) {
        if(!infoList.isEmpty()){
            throw HS3OfsExceptions.of("Do not support modify file", true);
        }

        if(segBuffer == null){
            segBuffer = ByteBuffer.allocate(MAX_SIZE_KB);
            Helper.OfsAssert((offset == 0), "File position is error");
        }

        buffer.flip();
        log.info("Position={},Remaining={}", buffer.position(), buffer.hasRemaining());
        while (buffer.hasRemaining()){
            if(!segBuffer.hasRemaining()){
                writeSegment();
            }
            int rl = segBuffer.remaining();
            if(rl > buffer.remaining()){
                rl = buffer.remaining();
            }
            segBuffer.put(buffer.array(), buffer.position(), rl);
            buffer.position(buffer.position() + rl);
        }
        return buffer.position();
    }

    @Override
    public void close() {
        if(!infoList.isEmpty()){
            return;
        }

        flush();
    }

    @Override
    public void delete() {

    }

    private void flush(){
        if((segBuffer == null) || (segBuffer.position() == 0)){
            return ;
        }

        writeSegment();
    }

    private void writeSegment(){
        Helper.OfsAssert((segBuffer.position() > 0), "segBuffer content is empty");
        log.info("write position:{}", segBuffer.position());
        String segName = String.format("%s_%d", this.path, offset);
        byte [] content = segBuffer.array();
        if(segBuffer.position() < MAX_SIZE_KB){
            content = Arrays.copyOf(segBuffer.array(), segBuffer.position());
        }

        String parent = spaceMeta.getUid() + "/" + spaceMeta.getPath();
        FileContentVo vo = FileContentVo.builder().content(content)
                .owner(usi.getOwner()).repo(usi.getRepo()).salt(spaceMeta.getSalt()).path(segName).parent(parent)
                .build();
        try{
            HS3Fs.createFile(vo, gitHub);
            offset += 1;
            segBuffer.clear();
        }catch (Exception ex){
            log.warn("Write segment={}, exceptions:{}", segName, ex.getMessage());
            throw HS3OfsExceptions.of(ex.getMessage());
        }
    }

    private int readSegment(){
        if(infoList.isEmpty()){
            return 0;
        }

        String segName = String.format("%s_%d", this.path, offset);
        TreeInfo info = infoList.get(offset);
        if(!info.getPath().endsWith(segName)){
            log.info("File segment error: {} !={}", info.getPath(), segName);
            throw HS3OfsExceptions.of(String.format("File segment is error:%s != %s", info.getPath(), segName));
        }

        if(segBuffer == null){
            segBuffer = ByteBuffer.allocate(MAX_SIZE_KB);
            Helper.OfsAssert((offset == 0), "File position is error");
        }else {
            segBuffer.clear();
        }

        Helper.OfsAssert((segBuffer.position() >= MAX_SIZE_KB), "Segment position is error");
        GetBlobVo vo = GetBlobVo.builder().owner(usi.getOwner()).repo(usi.getRepo())
                .salt(spaceMeta.getSalt()).sha(info.getSha()).build();
        try{
            HS3Fs.getBlob(vo, gitHub);
            segBuffer.put(vo.getContent());
            offset += 1;
            segBuffer.flip();
            return segBuffer.remaining();
        }catch (Exception ex){
            log.warn("Read segment={}, exceptions:{}", segName, ex.getMessage());
            throw HS3OfsExceptions.of(ex.getMessage());
        }
    }
}
