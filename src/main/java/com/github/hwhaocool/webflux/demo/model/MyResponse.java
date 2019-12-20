package com.github.hwhaocool.webflux.demo.model;

import java.text.DecimalFormat;

public class MyResponse {
    
    private long cost;
    
    private int count;
    
    private String message;
    
    private Integer statusCode;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    private String getRate() {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        double rate = (double)cost / (double) count;
        return decimalFormat.format(rate);
    }

    @Override
    public String toString() {
        return "Response [cost=" + cost + ", count=" + count + 
                ", rate=" + getRate() + " req/ms" + 
                ", message=" + message + ", statusCode=" + statusCode + 
                "]";
    }

    
    

}
