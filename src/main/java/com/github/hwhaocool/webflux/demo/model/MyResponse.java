package com.github.hwhaocool.webflux.demo.model;

public class MyResponse {
    
    private long cost;
    
    private int count;

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

    @Override
    public String toString() {
        return "MyResponse [cost=" + cost + ", count=" + count + "]";
    }

    
    

}
