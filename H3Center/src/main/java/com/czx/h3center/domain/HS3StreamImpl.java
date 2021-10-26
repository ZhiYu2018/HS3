package com.czx.h3center.domain;

import com.czx.h3common.util.EventBus;
import com.czx.h3common.util.TimeKickCache;
import com.czx.h3facade.dto.HSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
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
    private TimeKickCache<HS3SI> timeKickCache;
    private EventBus<String> eventBus;
    private ScheduledExecutorService service;

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

    }

    public void putLocalCache(HSObject hso){
        String key = String.format("%sFF%sFF%s", hso.getUid(), hso.getSpace(), hso.getFile());
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
            String [] files = CACHE_DIR.list((f,n) ->n.endsWith(".upq"));
            for(String file: files){
                int pos = file.indexOf("FF");
                if(pos < 0){
                    log.info("Delete a file:{}", file);
                    FileUtils.deleteQuietly(new File(file));
                    continue;
                }
                String uid = file.substring(pos);
                try{
                    eventBus.post(uid, file);
                }catch (Exception ex){
                    log.info("Post file={} exceptions:{}", file, ex.getMessage());
                    break;
                }
            }
        }
    }

    private void uploadFile(String fileName){
        log.info("Upload file:{}", fileName);
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
