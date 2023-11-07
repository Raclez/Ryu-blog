package com.example.demo.spider.processer;

import com.example.demo.spider.pipeline.BlogPipeline;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class BlogCrawler {
    @Autowired
    private BlogProcesser blogProcesser;
    @Autowired
    private BlogPipeline   blogPipeline;


    private static volatile Spider spider;



public  Spider getSpider() {

        if (spider == null) {
            synchronized (BlogProcesser.class) {
                if (spider == null) {

                    spider = Spider.create(blogProcesser)
                            .addUrl("https://www.csdn.net/")
                            .addPipeline(blogPipeline)
                            .setScheduler(new QueueScheduler()).
                            setExecutorService(Executors.newFixedThreadPool(10));


//                            .setSpiderListeners()
//                            .setScheduler(new RedisScheduler(new JedisPool(new GenericObjectPoolConfig(), "106.52.229.31", 6379, 50000 )));
//                .setDownloader(new HttpClientDownloader());


                    try {
                        SpiderMonitor.instance().register(spider);
                    } catch (JMException e) {
                        throw new RuntimeException(e);
                    }
                }
                }
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
