package com.czx.h3common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Slf4j
public class TimeKickCache <T>{
    private class CI{
        private String key;
        private T data;
        private long time;
    }

    private ConcurrentHashMap<String, CI> cacheMap;
    private LinkedList<CI> linkedList;
    private LinkedList<CI> kickLinkedList;
    private ReentrantLock lock;
    private Integer maxTime;
    private Consumer<T> consumer;
    private ScheduledExecutorService service;

    public TimeKickCache(int maxTime, TimeUnit timeUnit, Consumer<T> consumer){
        if(timeUnit != TimeUnit.SECONDS){
            throw new RuntimeException("timeUnit is not TimeUnit.SECONDS");
        }
        cacheMap = new ConcurrentHashMap<>();
        linkedList = new LinkedList<>();
        kickLinkedList = new LinkedList<>();
        lock = new ReentrantLock();
        this.maxTime = maxTime;
        this.consumer = consumer;
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(()->this.kickOf(), 10, 30, TimeUnit.SECONDS);
    }

    public T put(String key, T t){
        CI ci = cacheMap.get(key);
        if(ci != null){
            ci.data = t;
            ci.time = System.currentTimeMillis();
            return ci.data;
        }
        lock.lock();
        try{
            ci = cacheMap.get(key);
            if(ci != null){
                ci.data = t;
                ci.time = System.currentTimeMillis();
                return ci.data;
            }
            ci = new CI();
            ci.data = t;
            ci.time = System.currentTimeMillis();
            ci.key = key;
            cacheMap.put(key, ci);
            linkedList.add(ci);
            return null;
        }finally {
            lock.unlock();
        }
    }

    public T get(String key){
        CI ci = cacheMap.get(key);
        if(ci != null){
            ci.time = System.currentTimeMillis();
            return ci.data;
        }
        return null;
    }

    public void remove(String key){
        CI ci = cacheMap.remove(key);
        if(ci != null){
            ci.time = ci.time - maxTime * 1000;
        }
    }


    private void kickOf(){
        lock.lock();
        if(linkedList.size() > 0) {
            kickLinkedList.addAll(linkedList);
            linkedList.clear();
        }
        lock.unlock();
        if(kickLinkedList.isEmpty()){
            return ;
        }
        Iterator<CI> iterator = kickLinkedList.iterator();
        long now = System.currentTimeMillis();
        while (iterator.hasNext()){
            CI ci = iterator.next();
            int life = (int)(now - ci.time)/1000;
            if(life >= maxTime){
                iterator.remove();
                CI old = cacheMap.remove(ci.key);
                log.info("Kick off key={}, isExist={}, life={} sec", ci.key, (old != null), life);
                try{
                    consumer.accept(ci.data);
                }catch (Exception ex){
                }
            }
        }
    }
}
