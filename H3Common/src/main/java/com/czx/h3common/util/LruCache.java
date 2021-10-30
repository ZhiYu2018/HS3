package com.czx.h3common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LruCache<K,V>{
    class CacheItem<V>{
        V value;
        Long time;
    }
    private final int maxSize;
    private final int maxTimeLive;
    private ReadWriteLock lock;
    private LinkedHashMap<K,CacheItem<V>> cache;
    public LruCache(int maxSize, int maxTime){
        this.maxSize = maxSize;
        maxTimeLive = maxTime;
        lock = new ReentrantReadWriteLock();
        cache = new LinkedHashMap<K,CacheItem<V>>(100,0.75f){
            protected boolean removeEldestEntry(Map.Entry<K, CacheItem<V>> eldest){
                if(size() >= maxSize){
                    return true;
                }
                return ((System.currentTimeMillis() - eldest.getValue().time) >= maxTimeLive);
            }
        };
    }

    public V get(K key){
        Lock rl = lock.readLock();
        try{
            rl.lock();
            CacheItem<V> ci = cache.get(key);
            return ((ci != null)? ci.value:null);
        }finally {
            rl.unlock();
        }
    }

    public void clear(){
        cache.clear();
    }

    public V put(K key, V value){
        Lock wl = lock.writeLock();
        try{
            wl.lock();
            CacheItem<V> ci = cache.get(key);
            if(ci == null){
                ci = new CacheItem<>();
                ci.time = System.currentTimeMillis();
            }
            ci.value = value;
            ci = cache.put(key, ci);
            return ((ci != null)? ci.value:null);
        }finally {
            wl.unlock();
        }
    }
}
