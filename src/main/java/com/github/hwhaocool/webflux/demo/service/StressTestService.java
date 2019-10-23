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
        
        switch (type) {
        case "sync":
            requestBySyncMode(request);
            break;
            
        case "async":
            requestByAsyncMode(request);
            break;

        default:
            break;
        }
        
        return null;
    }
    
    private void requestBySyncMode(MyRequest request) {
        Integer count = request.getCount();
        
        HttpUriRequest httpRequest = genRequest(request);
        
        long start = System.currentTimeMillis();
        
        LOGGER.info("sync start, count {}, start {}", count, start);
        
        IntStream.range(0, count)
            .forEach(k -> invoke(httpRequest));
        
        LOGGER.info("sync end, cost {}", System.currentTimeMillis() - start);
    }
    
    private void requestByAsyncMode(MyRequest request)  {
        
        String method = request.getMethod();
        
        Integer count = request.getCount();
        
        HttpMethod httpMethod = HttpMethod.resolve(method.toUpperCase());
        
        URI uri = null;
        try {
            uri = new URI(request.getUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        LOGGER.info("async start, count {}, start {}", count, System.currentTimeMillis());
        
        RequestBodySpec requestBodySpec = WebClient.create()
            .method(httpMethod)
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("utf-8"));
        
        IntStream.range(0, count)
            .forEach(k -> invoke(requestBodySpec, k));
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
