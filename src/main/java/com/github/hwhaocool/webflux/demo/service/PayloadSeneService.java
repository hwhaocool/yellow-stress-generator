package com.github.hwhaocool.webflux.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import com.github.hwhaocool.webflux.demo.model.MyResponse;
import com.github.hwhaocool.webflux.demo.model.payload.FullRequest;
import com.github.hwhaocool.webflux.demo.model.payload.PayloadRquest;
import com.github.hwhaocool.webflux.demo.utils.HttpManager;

import reactor.core.publisher.Mono;

@Service
public class PayloadSeneService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadSeneService.class);
    
    //第一行， POST /stress/test?type=async HTTP/1.1
    private static final Pattern FIRST_LINE_PATTERN = Pattern.compile("^([a-zA-Z]+)\\s+([^\\s]+)\\s+HTTP/1.1\\s*$");
    
    //第二行， Host: stress-generator-service.middleware
    private static final Pattern SECOND_LINE_PATTERN = Pattern.compile("^host\\s*:\\s*(.+)$", Pattern.CASE_INSENSITIVE);
    
    //header， Content-Type: application/json
    private static final Pattern HEADER_PATTERN = Pattern.compile("^([^\\s:]+)\\s*:\\s*(.+)$");
    
    /**
     * <br>接收带 pyload 字符串的请求，解析、然后发送出去
     *
     * @param request
     * @return
     * @author YellowTail
     * @since 2019-12-19
     */
    public Mono<String> sendRequest(PayloadRquest request) {
        
        if (null  == request) {
            return Mono.just("request is required");
        }
        
        if (StringUtils.isBlank(request.getPayload())) {
            return Mono.just("payload is required");
        }
        
        String type = request.getType();
        
        MyResponse result = null;
        
        try {
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
        } catch (RuntimeException e) {
            return Mono.just(e.getMessage());
        }
        
        if (null == result) {
            return Mono.just("ok");
        }
        
        return Mono.just(result.toString());
    }
    
    private MyResponse requestBySyncMode(PayloadRquest request) {
        Integer count = request.getCount();
        
        MyResponse response = new MyResponse();
        response.setCount(count);
        
        
        HttpUriRequest httpRequest = genRequest(request);
        
        long start = System.currentTimeMillis();
        
        LOGGER.info("sync start, count {}, start {}", count, start);
        
        
        IntStream.range(0, count)
            .forEach(k -> invoke(httpRequest, response));
        
        long cost = System.currentTimeMillis() - start;
        
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
    private MyResponse requestByAsyncMode(PayloadRquest request)  {
        
        FullRequest fullRequest = convert(request);
        
        long start = System.currentTimeMillis();
        
        LOGGER.info("async start,  start at {}, request {}", start, request);
        
        HttpMethod httpMethod = HttpMethod.resolve(fullRequest.getMethod().toUpperCase());
        
        URI uri = null;
        try {
            uri = new URI(fullRequest.getFullUrl());
        } catch (URISyntaxException e) {
            LOGGER.error("parse uri error, ", e);
        }
        
        RequestBodySpec requestBodySpec = WebClient.create()
            .method(httpMethod)
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("utf-8"));
        
        IntStream.range(0, request.getCount())
            .forEach(k -> invoke(requestBodySpec, k));
        
        long cost = System.currentTimeMillis() - start;
        
        LOGGER.info("request submit completed, cost {}", cost);
        
        MyResponse response = new MyResponse();
        response.setCount(request.getCount());
        response.setCost(cost);
        
        return response;
    }
    
    private HttpUriRequest genRequest(PayloadRquest request) {
        
        FullRequest fullRequest = convert(request);
        
        return genRequest(fullRequest);
    }
    
    private HttpUriRequest genRequest(FullRequest fullRequest) {
        HttpUriRequest httpRequest = null;
        
        String url = fullRequest.getFullUrl();
        
        switch (fullRequest.getMethod()) {
        case "get":
            httpRequest = new HttpGet(url);
            
            break;
            
        case "put":
            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(genEntity(fullRequest.getData()));
            httpRequest = httpPut;
            break;
            
        case "post":
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(genEntity(fullRequest.getData()));
            httpRequest = httpPost;
            break;

        default:
            break;
        }
        
        for (Map.Entry<String, String> entry: fullRequest.getHeaderMap().entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue());
        }
        
        return httpRequest;
    }
    
    private StringEntity genEntity(String json) {
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }
    
    
    /**
     * <br>执行 http 请求
     *
     * @param httpRequest
     * @param response
     * @author YellowTail
     * @since 2019-12-20
     */
    private void invoke(HttpUriRequest httpRequest, MyResponse response) {
        
        try {
            HttpResponse httpResponse = HttpManager.DEFAULT_CLIENT.execute(httpRequest);
            
            if (null != response && 1 == response.getCount()) {
                //只发一次的时候，才会返回 response
                response.setMessage(EntityUtils.toString(httpResponse.getEntity(), "utf-8"));
            } else {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            
        } catch (IOException e) {
            LOGGER.error("not ok ", e);
            
            throw new RuntimeException(e.toString());
        }
    }
    
    private void invoke(RequestBodySpec requestBodySpec, int index) {
        long start = System.currentTimeMillis();
        
        requestBodySpec
            .retrieve()
            .bodyToMono(String.class)
//            .log()
            .doOnError(e -> {
                LOGGER.error("async occur error, {}", e.getClass().getName());
                
                LOGGER.error("error, ", e);
            })
            .subscribe(k -> LOGGER.info("{} cost {}", index, System.currentTimeMillis() - start))
//            .subscribe(k ->  {} )
            ;
    }
    
    private FullRequest convert(PayloadRquest request) {
        FullRequest fullRequest = new FullRequest();
        
        String payload = request.getPayload();
        
        // POST /stress/test?type=async HTTP/1.1
        
        String[] split = payload.split("\n");
        
        
        Matcher matcher = null;
        
        String host = null;
        
        matcher = FIRST_LINE_PATTERN.matcher(split[0]);
        if (matcher.matches()) {
            String method = matcher.group(1).toLowerCase();
            String uri = matcher.group(2);
            
            fullRequest.setMethod(method);
            fullRequest.setUri(uri);
            
        } else {
            throw new RuntimeException("method or uri is empty");
        }
        
        matcher = SECOND_LINE_PATTERN.matcher(split[1]);
        if (matcher.matches()) {
            host = matcher.group(1);
            fullRequest.setHost(host);
        } else {
            throw new RuntimeException("hots is empty");
        }
        
        //data
        StringBuilder json = new StringBuilder(256);
        boolean findJson = false;
        
        //header
        Map<String, String> headerMap = new HashMap<>();
        
        for (int i = 2; i < split.length; i++) {
            String string = split[i].trim();
            
            if (StringUtils.isBlank(string)) {
                continue;
            }
            
            if (findJson) {
                json.append(string);
                continue;
            }
            
            if (string.startsWith("{")) {
                //find json start
                findJson = true;
                json.append(string);
                continue;
            } else {
                //header
                matcher = HEADER_PATTERN.matcher(string);
                if (matcher.matches()) {
                    String key = matcher.group(1);
                    String value = matcher.group(2);
                    headerMap.put(key, value);
                }
            }
        }
        
        fullRequest.setHeaderMap(headerMap);
        fullRequest.setData(json.toString());
        
        return fullRequest;
    }
    
    

}
