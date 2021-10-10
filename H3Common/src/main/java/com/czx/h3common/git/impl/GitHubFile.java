package com.czx.h3common.git.impl;

import com.czx.h3common.git.GitHub;
import com.czx.h3common.git.HS3Fs;
import com.czx.h3common.git.dto.TreeDto;
import com.czx.h3common.git.vo.FileContentVo;
import com.czx.h3common.git.vo.TreeInfo;
import com.czx.h3common.util.Helper;
import com.czx.h3outbound.ofs.HS3File;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import com.czx.h3outbound.ofs.vo.FileMeta;
import com.czx.h3outbound.ofs.vo.FileSegment;
import com.czx.h3outbound.ofs.vo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Data
@Builder
@AllArgsConstructor
public class GitHubFile implements HS3File {
    private final static int MAX_SIZE_MB = 1024*1024;
    private String path;
    private UserInfo usi;
    private GitHub gitHub;
    private FileMeta spaceMeta;
    private ByteBuffer segBuffer;
    private int offset;
    private List<TreeInfo> infoList;

    @Override
    public void open(String dir, String path) {
        this.path = dir + "/" + path;
        offset = 0;
        try{
            TreeDto treeDto = gitHub.getGitTree(usi.getOwner(), usi.getRepo(), spaceMeta.getSha(), new HashMap<>());
            Stream<TreeInfo> stream = treeDto.getTree().stream().filter(r->r.getPath().startsWith(path));
            infoList = stream.collect(Collectors.toList());
        }catch (Exception ex){
            log.warn("Open dir={},path={} exceptions:{}", dir, path, ex.getMessage());
            throw HS3OfsExceptions.of(ex.getMessage());
        }
    }

    @Override
    public FileMeta meta() {
        AtomicInteger num = new AtomicInteger(1);
        FileMeta meta = FileMeta.builder().uid(meta().getUid()).path(path).build();
        Stream<FileSegment> stream = infoList.stream().map(r-> FileSegment.builder()
                .name(r.getPath()).sha(r.getSha()).num(num.getAndIncrement()).build());
        meta.setSegments(stream.collect(Collectors.toList()));
        return meta;
    }

    @Override
    public int read(ByteBuffer buffer, int len) {
        if(infoList.isEmpty()){
            throw HS3OfsExceptions.of("File is new");
        }

        int totalLen = 0;
        while((totalLen < len) && buffer.hasRemaining()){
            if((segBuffer == null) || !segBuffer.hasRemaining()){
                readSegment();
            }

            if(!segBuffer.hasRemaining()){
                break;
            }

            int rl = buffer.remaining();
            if(rl > segBuffer.remaining()){
                rl = segBuffer.remaining();
            }
            totalLen = totalLen + rl;
            buffer.put(segBuffer);
        }
        return totalLen;
    }

    @Override
    public int write(ByteBuffer buffer, int len) {
        if(!infoList.isEmpty()){
            throw HS3OfsExceptions.of("Do not support modify file");
        }
        int totalLen = 0;
        while ((totalLen < len) && buffer.hasRemaining()){
            if((segBuffer == null) || !segBuffer.hasRemaining()){
                writeSegment();
            }
        }
        return totalLen;
    }

    @Override
    public void close() {
        if(infoList.isEmpty()){
            return;
        }
    }

    private void writeSegment(){
        if(segBuffer == null){
            segBuffer = ByteBuffer.allocate(MAX_SIZE_MB);
            Helper.OfsAssert((offset == 0), "File position is error");
        }

        Helper.OfsAssert((segBuffer.position() >= MAX_SIZE_MB), "Segment position is error");
        int segNum = (offset/MAX_SIZE_MB);
        String segName = String.format("%s_%d", this.path, segNum);
        FileContentVo vo = FileContentVo.builder().content(segBuffer.array())
                .owner(usi.getOwner()).salt(spaceMeta.getSalt()).path(segName).parent(spaceMeta.getPath()).build();
        try{
            HS3Fs.createFile(vo, gitHub);
        }catch (Exception ex){
            log.warn("Write segment={}, exceptions:{}", segName, ex.getMessage());
            throw HS3OfsExceptions.of(ex.getMessage());
        }
    }

    private void readSegment(){
        segBuffer.reset();
    }
}
