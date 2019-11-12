package com.github.hwhaocool.webflux.demo.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stress")
public class HealthCheckController {
    
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

}
