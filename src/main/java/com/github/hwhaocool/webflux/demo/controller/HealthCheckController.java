package com.github.hwhaocool.webflux.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stress")
public class HealthCheckController {
    
    @GetMapping("/health")
    public Mono<String> health() {
        
        return Mono.just("ok");
    }

}
