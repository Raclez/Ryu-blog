package com.example.demo.spider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

//    @Value("${threadpool.corePoolSize}")
//    private int corePoolSize;
//
//    @Value("${threadpool.maxPoolSize}")
//    private int maxPoolSize;

//    @Value("${threadpool.keepAliveTime}")
//    private int keepAliveTime;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16); // 最大线程数可以根据实际情况进行调整
        executor.setQueueCapacity(100); // 队列大小也可以根据需求进行调整
        executor.setThreadNamePrefix("spider-threadpool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略
        executor.initialize();
        return executor;
    }
}
