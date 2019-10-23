package com.fanggeek.webflux.demo.model;

public class MyRequest {

    private String url;
    
    private String method = "get";
    
    private Integer count =  5;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "MyRequest [url=" + url + ", method=" + method + ", count=" + count + "]";
    }
    
    
    
}
