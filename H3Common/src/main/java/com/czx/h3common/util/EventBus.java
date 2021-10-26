package com.czx.h3common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class EventBus <T>{
    private Consumer<T> consumer;
    private List<BusThread<T>> list;
    public EventBus(String name, Consumer<T> consumer){
        this.consumer = consumer;
        list = new ArrayList<>();
        int p = Runtime.getRuntime().availableProcessors() * 2;
        for(int i = 0; i < p; i++){
            String threadName = String.format("BUS.%s.%d", name, i);
            BusThread busItem = new BusThread(threadName, consumer);
            list.add(busItem);
        }

    }

    public void post(String key, T t) throws Exception {
        int n = Math.abs(key.hashCode()) % list.size();
        BusThread busThread = list.get(n);
        busThread.put(t);
    }

    public void stop(){
        for(BusThread th:list){
            th.stop();
        }
    }
}
