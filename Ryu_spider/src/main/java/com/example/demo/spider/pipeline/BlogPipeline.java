package com.example.demo.spider.pipeline;


import com.example.demo.commons.entity.BlogSpider;
import com.example.demo.commons.feign.SearchFeignClient;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.commons.pojo.ESMessage;
import com.example.demo.spider.global.SysConf;
import com.example.demo.spider.processer.BlogProcesser;
import com.example.demo.spider.processer.LocalDataStorage;
import com.example.demo.spider.service.BlogSpiderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public List<BlogSpider> dataBuffer = new CopyOnWriteArrayList<>();
    private  List<BlogElasticsearchModel> list = new CopyOnWriteArrayList<>(); // 创建一个缓冲列表用于暂存数据
    public CountDownLatch countDownLatch;
    @Autowired
   private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LocalDataStorage localDataStorage;

    @Override
    public void process(ResultItems res, Task task) {
        BlogSpider blogSpider = res.get("blogSpider");
        BlogElasticsearchModel elasticsearchModel = res.get("elasticsearchModel");
        AtomicBoolean isSpider = res.get("isSpider");
        list.add(elasticsearchModel);
        dataBuffer.add(blogSpider);

        sendEsMessage(elasticsearchModel);
        if (isSpider.get()) {
            ArrayList<BlogSpider> blogSpiders = new ArrayList<>(dataBuffer);
            ArrayList<BlogElasticsearchModel> elasticsearchModels = new ArrayList<>(list);
            CompletableFuture<Void> saveToMysqlFuture = CompletableFuture.runAsync(() -> {
                blogSpiderService.saveBatch(blogSpiders);
            },threadPoolTaskExecutor);

//                CompletableFuture<Void> sendToESFuture = CompletableFuture.runAsync(() -> {
//                    byte[] bytes;
//                    try {
//                         bytes = objectMapper.writeValueAsBytes(elasticsearchModels);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Message message = MessageBuilder.withBody(bytes).setContentType(MessageProperties.CONTENT_TYPE_JSON).setHeader("retryCount",1).build();
//                    sendEsMessage(message);
//                }, threadPoolTaskExecutor);


//                CompletableFuture.runAsync(() ->{
//                    localDataStorage.generateMarkdownFiles(list);
//                },threadPoolTaskExecutor);
//            countDownLatch.countDown();

        }

    }
    public void  sendEsMessage(BlogElasticsearchModel elasticsearchModel){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("exchange.direct", SysConf.Ryu_BLOG, elasticsearchModel);
    }

public List<BlogElasticsearchModel> getData() {
    try {
        countDownLatch.await();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    List<BlogElasticsearchModel> blogElasticsearchModels = new ArrayList<>(list);


    list.clear();
    dataBuffer.clear();
    return  blogElasticsearchModels;
}
}
