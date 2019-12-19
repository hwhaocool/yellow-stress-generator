package com.github.hwhaocool.webflux.demo.model.payload;

public class PayloadRquest {
    
    /**
     * 次数
     */
    private Integer count;
    
    /**
     * 并发数
     */
    private Integer concurrent;
    
    /**
     * payload
     */
    private String payload;
    
    /**
     * 类型， 同步还是异步
     */
    private String type;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Integer concurrent) {
        this.concurrent = concurrent;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PayloadRquest [count=" + count + ", concurrent=" + concurrent + ", payload=" + payload + ", type="
                + type + "]";
    }
    
    

}
