package com.czx.h3common.security;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class TinkKeyManager {
    private final static long FILE_TIME_LIVE_SEC = 3600 * 24;
    private final static String path = "/tink/key";
    public static KeysetHandle getKeySetHandle(String name){
        File pathDir = new File(path);
        if(!pathDir.exists()){
            log.info("Mkdirs ={}, Return:{}", path, pathDir.mkdirs());
        }
        File keyFile = getNewFile(new File(path, name));
        if(keyFile == null){
            return null;
        }
        try{
            KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keyFile));
            log.info("Load Key set Handle from {} success", name);
            return keysetHandle;
        }catch (Exception ex){
            log.info("Read KeySet exception {}:{}", name, ex.getMessage());
            return null;
        }
    }

    public static void StoringKeys(String name, KeysetHandle keysetHandle){
        try {
            File subDir = new File(path, name);
            subDir.mkdirs();
            String fileName = String.format("%s.json", name);
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(new File(subDir, fileName)));
            log.info("StoringKeys name={}, fileName={} OK", name, fileName);
        }catch (Exception t){
            log.info("StoringKeys name={}, exceptions:{}", name, t.getMessage());
        }
    }

    private static File getNewFile(File subDir){
        if(!subDir.exists()){
            log.info("Dir={} is not exist", subDir.getAbsolutePath());
            return null;
        }

        File[] dirs = subDir.listFiles();
        if(dirs.length == 0){
            log.info("Dir={} is empty", subDir.getAbsolutePath());
            return null;
        }
        File target = null;
        long time = 0;
        for(File file:dirs){
            if(file.lastModified() > time){
                target = file;
                time = file.lastModified();
                continue;
            }
        }
        return target;
    }
}
