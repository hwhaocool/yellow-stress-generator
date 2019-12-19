package com.github.hwhaocool.webflux.demo.model.payload;

import java.util.Map;

public class FullRequest {
    
    private String host;
    
    private String uri;
    
    private String method;
    
    private String data;
    
    private Map<String, String> headerMap;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    @Override
    public String toString() {
        return "FullRequest [host=" + host + ", uri=" + uri + ", method=" + method + ", data=" + data + ", headerMap="
                + headerMap + "]";
    }
    
    

}
