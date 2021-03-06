package com.github.hwhaocool.webflux.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.hwhaocool.webflux.demo.model.MyRequest;
import com.github.hwhaocool.webflux.demo.model.MyResponse;
import com.github.hwhaocool.webflux.demo.model.payload.PayloadRquest;
import com.github.hwhaocool.webflux.demo.service.PayloadSeneService;
import com.github.hwhaocool.webflux.demo.service.StressTestService;

import reactor.core.publisher.Mono;

/**
 * <br>
 * 接口压力测试 接口
 *
 * @author YellowTail
 * @since 2019-10-18
 */
@RestController
@RequestMapping("/stress")
public class StressTestController {

    @Autowired
    private StressTestService stressTestService;
    
    @Autowired
    private PayloadSeneService payloadRquest;

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "ha ha")
    @ExceptionHandler(RuntimeException.class)
    public void notFound() {

    }

    @PostMapping(value="/test", produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<MyResponse> request(@RequestParam("type") final String type, @RequestBody final MyRequest request) {
        return stressTestService.sendRequest(type, request);
    }
    
    @PutMapping(value="/send", produces=MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> request(@RequestBody final PayloadRquest request) {
        return payloadRquest.sendRequest(request);
    }

}
