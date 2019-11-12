package com.github.hwhaocool.webflux.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.github.hwhaocool.webflux.demo.model.MyRequest;
import com.github.hwhaocool.webflux.demo.model.MyResponse;
import com.github.hwhaocool.webflux.demo.utils.HttpManager;

import reactor.core.publisher.Mono;

@Service
public class StressTestService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StressTestService.class);
    
    private long startTime;
    
    private Map<Long, Integer> map;
    
    public StressTestService() {
        startTime = System.currentTimeMillis();
        
        map = new ConcurrentHashMap<>(5000);
    }

    public Mono<MyResponse> sendRequest(String type, MyRequest request) {
        if (null == type || type.isEmpty()) {
            type = "sync";
        }
        
        MyResponse result = null;
        
        switch (type) {
        case "sync":
            result = requestBySyncMode(request);
            break;
            
        case "async":
            result = requestByAsyncMode(request);
            break;

        default:
            break;
        }
        
        return Mono.just(result);
    }
    
    private MyResponse requestBySyncMode(MyRequest request) {
        Integer count = request.getCount();
        
        HttpUriRequest httpRequest = genRequest(request);
        
        long start = System.currentTimeMillis();
        
        LOGGER.info("sync start, count {}, start {}", count, start);
        
        IntStream.range(0, count)
            .forEach(k -> invoke(httpRequest));
        
        long cost = System.currentTimeMillis() - start;
        
        MyResponse response = new MyResponse();
        response.setCount(count);
        response.setCost(cost);
        
        LOGGER.info("sync end, cost {}", cost);
        
        return response;
    }
    
    /**
     * <br>使用异步模式去发送请求
     *
     * @param request
     * @author YellowTail
     * @since 2019-10-23
     */
    private MyResponse requestByAsyncMode(MyRequest request)  {
        String method = request.getMethod();
        
        Integer count = request.getCount();
        
        long start = System.currentTimeMillis();
        
        LOGGER.info("async start, count {}, start at {}, request {}", 
                count, start, request);
        
        HttpMethod httpMethod = HttpMethod.resolve(method.toUpperCase());
        
        URI uri = null;
        try {
            uri = new URI(request.getUrl());
        } catch (URISyntaxException e) {
            LOGGER.error("parse uri error, ", e);
        }
        
        RequestBodySpec requestBodySpec = WebClient.create()
            .method(httpMethod)
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("utf-8"));
        
        IntStream.range(0, count)
            .forEach(k -> invoke(requestBodySpec, k));
        
        long cost = System.currentTimeMillis() - start;
        
        LOGGER.info("request submit completed, cost {}", cost);
        
        MyResponse response = new MyResponse();
        response.setCount(count);
        response.setCost(cost);
        
        return response;
    }
    
    private void invoke(HttpUriRequest httpRequest) {
        
        try {
            HttpResponse httpResponse = HttpManager.DEFAULT_CLIENT.execute(httpRequest);
            
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            
            if (HttpStatus.OK.value() == statusCode) {
            } else {
//                LOGGER.error("not ok {}", statusCode);
            }
            
            String string = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            
//            LOGGER.info(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void invoke(RequestBodySpec requestBodySpec, int index) {
        long start = System.currentTimeMillis();
        
        requestBodySpec
            .retrieve()
            .bodyToMono(String.class)
//            .log()
//            .subscribe(k -> LOGGER.info("{} cost {}", index, System.currentTimeMillis() - start))
            
            .doOnError(e -> {
                LOGGER.error("async occur error, {}", e.getClass().getName());
                
                LOGGER.error("error, ", e);
            })
            .subscribe(k ->  map.put(System.currentTimeMillis(), index) )
            ;
    }
    
    private HttpUriRequest genRequest(MyRequest request) {
        String url = request.getUrl();
        
        String method = request.getMethod().toLowerCase();
        
        HttpUriRequest httpRequest = null;
        
        switch (method) {
        case "get":
            httpRequest = new HttpGet(url);
            break;
            
        case "put":
            HttpPut httpPut = new HttpPut(url);
            httpRequest = httpPut;
            break;
            
        case "post":
            HttpPost httpPost = new HttpPost(url);
            httpRequest = httpPost;
            break;

        default:
            break;
        }
        
        return httpRequest;
    }
    
    public void printMap() {
        
        map.entrySet()
            .stream()
            .sorted(Comparator.comparing(  k -> {
                return k.getKey();
            }))
            .findFirst()
            .ifPresent(System.out::println);
            ;
    }
}
