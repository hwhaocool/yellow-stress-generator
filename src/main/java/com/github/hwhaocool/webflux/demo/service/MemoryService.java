package com.github.hwhaocool.webflux.demo.service;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemoryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadSeneService.class);
    
    private static final String CHANGE_LINE = "\n";
    
    public static void main(String[] args) {
        String mem = new MemoryService().mem();
        
        System.out.println(mem);
    }
    
    /**
     * <br>得到 内存信息
     * <br>代码基本抄了 阿里的 arthas 
     * <br>https://github.com/alibaba/arthas/blob/21ce2dd324300e852bc6063f5a4625c54b88fb2c/core/src/main/java/com/taobao/arthas/core/command/monitor200/DashboardCommand.java
     *
     * @return
     * @author YellowTail
     * @since 2019-12-21
     */
    public String mem() {
        StringBuilder sb = new StringBuilder(1024);
        
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        
        sb.append("heap -> ").append(humanRead(heapMemoryUsage)).append(CHANGE_LINE);

        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = poolMXBean.getUsage();
                String poolName = beautifyName(poolMXBean.getName());
                
                sb.append(poolName).append(" ->").append(humanRead(usage)).append(CHANGE_LINE);
            }
        }
        
        sb.append("noheap -> ").append(humanRead(nonHeapMemoryUsage)).append(CHANGE_LINE);

        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.NON_HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = poolMXBean.getUsage();
                String poolName = beautifyName(poolMXBean.getName());
                
                sb.append(poolName).append(" ->").append(humanRead(usage)).append(CHANGE_LINE);
            }
        }
        
        LOGGER.info("start to get direct info");
        
        try {
            @SuppressWarnings("rawtypes")
            Class bufferPoolMXBeanClass = Class.forName("java.lang.management.BufferPoolMXBean");
            @SuppressWarnings("unchecked")
            List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(bufferPoolMXBeanClass);
            for (BufferPoolMXBean mbean : bufferPoolMXBeans) {
                long used = mbean.getMemoryUsed();
                long total = mbean.getTotalCapacity();
                
                sb.append(mbean.getName()).append(" -> ")
                    .append("used: ").append(humanRead(used)).append(", ")
                    .append("total: ").append(humanRead(total)).append(CHANGE_LINE);
                
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        
        return sb.toString();
    }
    
    private String beautifyName(String name) {
        return name.replace(' ', '_').toLowerCase();
    }
    
    private String humanRead(long value) {
        if (value < 1024) {
            return value + "Byte";
        }
        
        value = value >> 10;
        if (value < 1024) {
            return value + "K";
        }
        
        value = value >> 10;
        if (value < 1024) {
            return value + "M";
        }
        
        value = value >> 10;
        return value + "G";
    }
    
    private String humanRead(MemoryUsage usage) {
        StringBuffer buf = new StringBuffer(" ");
        
        buf.append(String.format("%s = %d(%s) ", "init", usage.getInit(), humanRead(usage.getInit())));
        buf.append(String.format("%s = %d(%s) ", "used", usage.getUsed(), humanRead(usage.getUsed())));
        buf.append(String.format("%s = %d(%s) ", "committed", usage.getCommitted(), humanRead(usage.getCommitted())));
        buf.append(String.format("%s = %d(%s) ", "max", usage.getMax(), humanRead(usage.getMax())));
        
        return buf.toString();
    }

}
