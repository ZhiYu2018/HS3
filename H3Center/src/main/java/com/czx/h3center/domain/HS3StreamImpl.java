package com.czx.h3center.domain;

import com.czx.h3center.HS3Properties;
import com.czx.h3common.git.HS3Fs;
import com.czx.h3common.git.HS3Storage;
import com.czx.h3common.util.EventBus;
import com.czx.h3common.util.TimeKickCache;
import com.czx.h3facade.dto.HSObject;
import com.czx.h3outbound.ofs.HS3File;
import com.czx.h3outbound.ofs.HS3FileSystem;
import com.czx.h3outbound.ofs.HS3OfsExceptions;
import com.czx.h3outbound.ofs.vo.FileMeta;
import com.czx.h3outbound.ofs.vo.StorageType;
import com.czx.h3outbound.ofs.vo.UserInfo;
import com.czx.h3outbound.repository.HomeNasDaoI;
import com.czx.h3outbound.repository.dto.HomeNasDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HS3StreamImpl {
    class HS3SI{
        FileOutputStream fos;
        String key;
        int status;
        HS3SI(String k, FileOutputStream os){
            key = k;
            fos = os;
            status = -1;
        }
    }
    private static final int MAX_TIME = 600;
    private static final File CACHE_DIR = new File("/opt/hs3/cache");
    @Autowired
    private HS3Storage hs3Storage;
    @Autowired
    private HomeNasDaoI homeNasDao;

    private TimeKickCache<HS3SI> timeKickCache;
    private EventBus<String> eventBus;
    private ScheduledExecutorService service;
    private volatile CountDownLatch latch;

    public HS3StreamImpl(){
        timeKickCache = new TimeKickCache<>(MAX_TIME, TimeUnit.SECONDS, HS3StreamImpl::kickOf);
        eventBus = new EventBus<>("HS3Up", this::uploadFile);
        try{
            FileUtils.forceMkdir(CACHE_DIR);
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(()->this.scanDir(), 10, 60, TimeUnit.SECONDS);
        }catch (Exception ex){
            log.info("forceMkdir dir={} exceptions:{}", CACHE_DIR, ex.getMessage());
        }
        latch = null;
    }

    public void putLocalCache(HSObject hso){
        String key = String.format("%s#%s#%s", hso.getUid(), hso.getSpace(), hso.getFile());
        HS3SI hs3SI;
        try {
            hs3SI = timeKickCache.get(key);
            if (hs3SI == null) {
                FileOutputStream fos = FileUtils.openOutputStream(new File(CACHE_DIR, key + ".tmp"));
                hs3SI = new HS3SI(key, fos);
                timeKickCache.put(key, hs3SI);
            }
            log.info("Write file={}.{}.{}, num={}, len={}", hso.getUid(), hso.getSpace(), hso.getFile(),
                    hso.getNumber(), hso.getContent().length);
            hs3SI.fos.write(hso.getContent());
            if(hso.getIsLast()){
                log.info("Key={} is last", key);
                hs3SI.status = 0;
                timeKickCache.remove(key);
            }
        }catch (Exception ex){
           timeKickCache.remove(key);
        }
    }

    private void scanDir(){
        if(CACHE_DIR.isDirectory()){
            String [] files = CACHE_DIR.list((f,n) -> n.endsWith(".upq"));
            if(files.length == 0){
                return ;
            }

            latch = new CountDownLatch(files.length);
            for(String file: files){
                int pos = file.indexOf("#");
                if(pos < 0){
                    log.info("Delete a file:{}", file);
                    FileUtils.deleteQuietly(new File(CACHE_DIR, file));
                    continue;
                }
                String uid = file.substring(0, pos);
                try{
                    eventBus.post(uid, file);
                }catch (Exception ex){
                    log.info("Post file={} exceptions:{}", file, ex.getMessage());
                    break;
                }
            }
            /**等待处理完**/
            awaitQuietly();
            log.info("Post is finish ......");
            UserInfo usi = UserInfo.builder().owner(HS3Properties.getOwner()).repo(HS3Properties.getRepo())
                    .token(HS3Properties.getToken()).type(StorageType.ST_GITHUB).build();
            HS3FileSystem hs3FileSystem = hs3Storage.getHs3FileSystem(usi);
            hs3FileSystem.clear();
        }
    }

    private void uploadFile(String fileName){
        log.info("Upload file:{}", fileName);
        String [] info = fileName.split("#");
        if(info.length != 3){
            log.info("File={} is error", fileName);
            FileUtils.deleteQuietly(new File(CACHE_DIR, fileName));
            return ;
        }

        String uid = info[0];
        String space = info[1];
        String file = info[2].substring(0, info[2].length() - ".upq".length());
        File upqFile = new File(CACHE_DIR, fileName);
        if(!upqFile.exists()){
            return;
        }

        File upFile = new File(CACHE_DIR, String.format("%s#%s#%s.up", uid, space, file));
        upqFile.renameTo(upFile);

        try{
            HomeNas homeNas = HomeNas.getHomeNas(uid, homeNasDao);
            List<HomeNasDto> nasDtoList = homeNas.getSpaceMeta();
            HomeNasDto homeNasDto = null;
            for(HomeNasDto dto:nasDtoList){
                if(dto.getHome().equals(space)){
                    homeNasDto = dto;
                }
            }

            if(homeNasDto == null){
                log.info("Uid={},Space={} is not exist", uid, space);
                FileUtils.deleteQuietly(new File(CACHE_DIR, fileName));
                return;
            }

            UserInfo usi = UserInfo.builder().owner(HS3Properties.getOwner()).repo(HS3Properties.getRepo())
                    .token(HS3Properties.getToken()).type(StorageType.ST_GITHUB).build();
            HS3FileSystem hs3FileSystem = hs3Storage.getHs3FileSystem(usi);
            List<FileMeta> spaceMetas = hs3FileSystem.listHome(uid);
            if((spaceMetas == null) || spaceMetas.isEmpty()){
                log.info("Uid={},Space={} is not exist", uid, space);
                FileUtils.deleteQuietly(new File(CACHE_DIR, fileName));
                return;
            }

            FileMeta fileMeta = null;
            for(FileMeta fm:spaceMetas){
                log.info("Path={}", fm.getPath());
                if(fm.getPath().equals(space)){
                    log.info("Find space meta:{}", space);
                    fileMeta = fm;
                    break;
                }
            }

            if(fileMeta == null){
                log.info("Uid={},Space={} file meta is not exist", uid, space);
                FileUtils.deleteQuietly(new File(CACHE_DIR, fileName));
                return;
            }

            fileMeta.setSalt(homeNasDto.getSalt());
            HS3File hs3Fs = hs3FileSystem.open(fileMeta);
            uploadStream(upFile, file, hs3Fs);
        }catch (Exception ex){
            log.info("Upload uid={},space={},file={},ex={}", uid, space, file, ex.getMessage());
            if(ex instanceof HS3OfsExceptions){
                HS3OfsExceptions hs = (HS3OfsExceptions)ex;
                if(hs.isCanIgnore()){
                    return;
                }
            }
            upFile.renameTo(upqFile);
        }finally {
            latch.countDown();
        }
    }

    private void awaitQuietly(){
        try{
            latch.await();
        }catch (Exception ex){

        }
    }

    private static void uploadStream(File upFile, String file, HS3File hs3Fs) throws Exception {
        hs3Fs.open(file);
        log.info("uploadStream {} begin ....", file);
        try(FileInputStream fis = FileUtils.openInputStream(upFile)){
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int sendLen = 0;
            while (true){
                int len = fis.read(buffer.array());
                if(len == 0){
                    break;
                }
                buffer.position(len);
                hs3Fs.write(buffer, len);
                sendLen += len;
                if(len < buffer.capacity()){
                    log.info("Len={} is < {}", len, buffer.capacity());
                    break;
                }
            }
            hs3Fs.close();
            log.info("uploadStream {} finish, sendLen={}", file, sendLen);
        }catch (Exception ex){
            log.warn("uploadStream file={}, exceptions:{}", file, ex.getMessage());
            hs3Fs.delete();
            throw ex;
        }
    }

    private static void kickOf(HS3SI hs3SI) {
        if (hs3SI != null) {
            IOUtils.closeQuietly(hs3SI.fos);
            File old = new File(CACHE_DIR, hs3SI.key + ".tmp");
            if (hs3SI.status != 0) {
                FileUtils.deleteQuietly(old);
            } else {
                old.renameTo(new File(CACHE_DIR,hs3SI.key + ".upq"));
            }
        }
    }
}
