package com.czx.h3common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
class BusThread<T> implements Runnable{
    private volatile boolean isExist;
    private Consumer<T> consumer;
    private LinkedBlockingDeque<T> dataList;
    private Thread thread;
    public BusThread(String name, Consumer<T> consumer){
        this.isExist = false;
        this.consumer = consumer;
        dataList = new LinkedBlockingDeque<>();
        thread = new Thread(this, name);
        thread.start();
    }

    public void put(T data) throws Exception {
        dataList.put(data);
    }

    @Override
    public void run() {
        while (!isExist){
           T data = poll();
           if(data == null){
               continue;
           }
           try{
               consumer.accept(data);
           }catch (Exception ex){
               log.info("Consumer accept exceptions:{}", ex.getMessage());
           }
        }
    }

    private T poll(){
        try{
            return dataList.pollFirst(5, TimeUnit.SECONDS);
        }catch (Exception ex){
            return null;
        }
    }

    public void stop(){
        isExist = true;
        try{
            put(null);
        }catch (Exception ex){

        }
    }
}
