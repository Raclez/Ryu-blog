package com.example.demo.spider.pipeline;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.example.demo.commons.feign.SearchFeignClient;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.commons.pojo.ESMessage;
import com.example.demo.spider.entity.BlogSpider;
import com.example.demo.spider.global.SysConf;
import com.example.demo.spider.mapper.BlogSpiderMapper;
import com.example.demo.spider.util.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 博客传输管道
 *
 * @author
 * @date 2021年1月8日16:40:07
 */
@Component
public class BlogPipeline implements Pipeline {


    private final String SAVE_PATH = "D:\\blog\\sp";
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private BlogSpiderMapper blogSpiderMapper;
    @Autowired
    SearchFeignClient searchFeignClient;
    @Autowired
    RabbitTemplate rabbitTemplate;

    public ConcurrentHashSet<BlogSpider> dataBuffer = new ConcurrentHashSet<>(); // 使用 BlockingQueue 来实现更细粒度的同步
    private List<BlogElasticsearchModel> list = new CopyOnWriteArrayList<>(); // 创建一个缓冲列表用于暂存数据

    @Override
    public void process(ResultItems res, Task task) {
//    BlogElasticsearchModel blogElasticsearchModel = res.get("elasticsearchModel");
//        BlogSpider data = res.get("blogSpider");
//      list.add(blogElasticsearchModel);
//      dataBuffer.add(data);
//      if(dataBuffer.size()==10){
//          dataBuffer.clear();
//          list.clear();
//      }

//        ESMessage esMessage = new ESMessage();
//        esMessage.setData(list1);
//        esMessage.setOperation(SysConf.ADD);
//        sendEsMessage(esMessage);

    }
//    public void  sendEsMessage(ESMessage esMessage){
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//        rabbitTemplate.convertAndSend("exchange.direct", SysConf.Ryu_BLOG,esMessage);
//
//
//    }

}
