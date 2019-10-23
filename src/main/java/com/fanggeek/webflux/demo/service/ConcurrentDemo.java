package com.fanggeek.webflux.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConcurrentDemo {
    
    @Autowired
    private RedisService redisService;
    
    // key--城市, value--积分
    private Map<String, Integer> map;
    
    private Lock lock = new ReentrantLock();
    
    // 用于标识内存配置是否已经刷新
    private static boolean isHandle = false;
    
    //评论之后, 根据当前城市，奖励对应积分
    public void subPoints() {
        
        if(! isHandle) { //
            lock.lock(); // 获得锁
            if(! isHandle) {
                
              //刷新 内存缓存 且删除key
                refreshCacheAndDelRedisKey();
            }
        }
        
        Random random = new Random();
        if (random.nextBoolean()) {
            Integer points = getCurrentPoints();
            
            addPoints(points);
        }
    }
    
    synchronized private void refreshCacheAndDelRedisKey() {
        String value = redisService.get("key");
        if (null == value ||  value.isEmpty()) {
            return;
        }
        
        map = new HashMap<>();
        
        redisService.del("key");
    }
    
    public Integer getCurrentPoints() {
        String currentCity = getCurrentCity();
        
        Integer points = map.get(currentCity);
        
        return points;
    }
    
    private String getCurrentCity() {
        return null;
    }
    
    private void addPoints(Integer points) {
    }
}
