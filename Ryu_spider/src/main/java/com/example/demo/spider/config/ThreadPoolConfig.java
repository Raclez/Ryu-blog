package com.example.demo.spider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
//        executor.setKeepAliveSeconds();
//        executor.setKeepAliveSeconds(keepAliveTime);
//        executor.setKeepAliveSeconds(30);
        // 可根据需要设置更多属性，例如队列容量、线程前缀等

        return executor;
    }
}
