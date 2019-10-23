package com.github.hwhaocool.webflux.demo.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpManager {

    private static final int MAX_CONN_TOTAL = 800;   //设置最大连接数,默认值是2
    
    private static final int MAX_CONN_PER_ROUTE = 400;   //每个路由最大连接数(一个域名就是一个连接)
    
    private static final int CONNECTION_POOL_TIMEOUT = 1000;   //从连接池中获取连接的超时时间, 1000毫秒
    
    private static final int CONNECTION_TIMEOUT = 5000;       //与服务器连接超时, 5000毫秒
    private static final int SOCKET_TIMEOUT = 5000;           //读取服务器返回的数据 超时, 5000毫秒
    
    /**
     * <br>默认提供一个 client
     */
    public static final HttpClient DEFAULT_CLIENT;
    
    static {
        RequestConfig clusterConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_POOL_TIMEOUT)
                .setConnectTimeout(CONNECTION_TIMEOUT)               // 与服务器连接超时
                .setSocketTimeout(SOCKET_TIMEOUT)                // 读取服务器返回的数据 超时
                .build();
        
        //参数含义参考 下面两个博客
        //https://blog.csdn.net/shootyou/article/details/6615051
        //https://blog.csdn.net/shootyou/article/details/6415248
        
        DEFAULT_CLIENT = HttpClientBuilder.create()
            .setDefaultRequestConfig(clusterConfig)
            .setMaxConnTotal(MAX_CONN_TOTAL)
            .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
            .build();
    }
}
