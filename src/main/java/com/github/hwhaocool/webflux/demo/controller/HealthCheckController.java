package com.github.hwhaocool.webflux.demo.controller;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stress")
public class HealthCheckController {
    
    private static final String CHANGE_LINE = "\n";
    
    @GetMapping(value="/health", produces=MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> health() {
        
        return Mono.just("ok");
    }
    
    @GetMapping("/block1h")
    public Mono<String> block1h() {
        
        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return Mono.just("ok");
    }
    
    @GetMapping("/mem")
    public Mono<String> mem() {
        
        StringBuilder sb = new StringBuilder(256);
        
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        
        sb.append("heap ->").append(heapMemoryUsage.toString()).append(CHANGE_LINE);

        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = poolMXBean.getUsage();
                String poolName = beautifyName(poolMXBean.getName());
                
                sb.append(poolName).append("->").append(usage.toString()).append(CHANGE_LINE);
            }
        }

        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.NON_HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = poolMXBean.getUsage();
                String poolName = beautifyName(poolMXBean.getName());
                
                sb.append(poolName).append("->").append(usage.toString()).append(CHANGE_LINE);
            }
        }
        
        return Mono.just(sb.toString());
    }
    
    private static String beautifyName(String name) {
        return name.replace(' ', '_').toLowerCase();
    }

}
