package com.example.demo.spider.processer;


import com.example.demo.spider.pipeline.BlogPipeline;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class BlogCrawler {
    @Autowired
    private BlogProcesser blogProcesser;
    @Autowired
    private BlogPipeline   blogPipeline;

    @Autowired
    ThreadPoolTaskExecutor ThreadPoolTaskExecutor;
    private Spider spider;



public  Spider getSpider() {

        if (spider == null) {
                    spider = Spider.create(blogProcesser)
                            .addUrl("https://blog.csdn.net/")
                            .addPipeline(blogPipeline)
                            .setExecutorService(ThreadPoolTaskExecutor.getThreadPoolExecutor())
//                            .setExecutorService(Executors.newFixedThreadPool(10))
                            .setScheduler(new RedisScheduler(new JedisPool(new GenericObjectPoolConfig(), "ryu.asia", 6379, 50000 ,"475118582")));
//                            .setDownloader(new HttpClientDownloader());
            }

        return spider;
        }


    public void startCrawling() {
        spider.start();
    }

    public void stopCrawling() {
        if (spider != null) {
            spider.stop();
        }
    }
}
