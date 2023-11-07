package com.example.demo.spider.pipeline;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.example.demo.commons.feign.SearchFeignClient;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.commons.pojo.ESMessage;
import com.example.demo.spider.entity.BlogSpider;
import com.example.demo.spider.global.SysConf;
import com.example.demo.spider.mapper.BlogSpiderMapper;
import com.example.demo.spider.processer.BlogProcesser;
import com.example.demo.spider.service.BlogSpiderService;
import com.example.demo.spider.util.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 博客传输管道
 *
 * @author
 * @date 2021年1月8日16:40:07
 */
@Component
public class BlogPipeline implements Pipeline {


    @Autowired
   private BlogSpiderService blogSpiderService;
    @Autowired
    SearchFeignClient searchFeignClient;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    BlogProcesser blogProcesser;

    public  ConcurrentHashSet<BlogSpider> dataBuffer = new ConcurrentHashSet<>(); // 使用 BlockingQueue 来实现更细粒度的同步
    private  List<BlogElasticsearchModel> list = new CopyOnWriteArrayList<>(); // 创建一个缓冲列表用于暂存数据
    public CountDownLatch countDownLatch;
    @Autowired
   private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void process(ResultItems res, Task task) {
        BlogSpider blogSpider = res.get("blogSpider");
        BlogElasticsearchModel elasticsearchModel = res.get("elasticsearchModel");
        boolean isSpider = res.get("isSpider");
        list.add(elasticsearchModel);
        if (isSpider == true) {
            countDownLatch.countDown();


//            CompletableFuture<Void> saveToMysqlFuture = CompletableFuture.runAsync(() -> {
//                blogSpiderService.saveBatch(dataBuffer);
//            },threadPoolTaskExecutor);
//        }


        }
//    public void  sendEsMessage(ESMessage esMessage){
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//        rabbitTemplate.convertAndSend("exchange.direct", SysConf.Ryu_BLOG,esMessage);
//


//                CompletableFuture<Void> sendToESFuture = CompletableFuture.runAsync(() -> {
//                    ESMessage esMessage = new ESMessage();
//                    esMessage.setData(list);
//                    esMessage.setOperation(SysConf.ADD);
////                    sendEsMessage(esMessage);
//
//                }, executorService);
//
//                CompletableFuture<Void> allOf = CompletableFuture.allOf(saveToMysqlFuture, sendToESFuture);
//                allOf.join(); // 等待两个任务都完成

//
//    }
    }
public List<BlogElasticsearchModel> getData() {
    try {
        countDownLatch.await();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    ArrayList<BlogElasticsearchModel> blogElasticsearchModels = new ArrayList<>(list);
    list.clear();
    dataBuffer.clear();
    return blogElasticsearchModels;
}
}
