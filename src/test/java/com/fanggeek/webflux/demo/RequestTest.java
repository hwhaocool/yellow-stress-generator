package com.fanggeek.webflux.demo;

import com.fanggeek.webflux.demo.model.MyRequest;
import com.fanggeek.webflux.demo.service.StressTestService;

public class RequestTest {
    
    private StressTestService service;
    
    public RequestTest(StressTestService service) {
        this.service = service;
    }

    public static void main(String[] args) throws InterruptedException {
        StressTestService service = new StressTestService();
        
        RequestTest requestTest = new RequestTest(service);
        requestTest.async();
        
        System.out.println();
        
        requestTest.sync();
        
        requestTest.printMap();
        
        System.out.println("completed");
        
    }
    
    public void sync() {
        long currentTimeMillis = System.currentTimeMillis();
        
        service.sendRequest("sync", genRequest());
        
        System.out.println("\n sync = " +  ( System.currentTimeMillis() - currentTimeMillis));
        
    }
    
    public void async() {
        long currentTimeMillis = System.currentTimeMillis();
        
        service.sendRequest("async", genRequest());
        
        service.printMap();
        
        System.out.println("async = " +  ( System.currentTimeMillis() - currentTimeMillis));
    }
    
    private MyRequest genRequest() {
        MyRequest myRequest = new MyRequest();
        myRequest.setMethod("get");
        myRequest.setCount(200);
        myRequest.setUrl("http://api-b-dev.kanfangjilu.com/v1/visitcard/estatecompanys");
        
        System.out.println("count = " + myRequest.getCount());
        
        return myRequest;
    }
    
    public void printMap() {
        service.printMap();
    }
}
