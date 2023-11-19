package com.example.demo.spider.restapi;


import cn.hutool.core.collection.ConcurrentHashSet;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.spider.pipeline.BlogPipeline;
import com.example.demo.spider.processer.BlogCrawler;
import com.example.demo.spider.processer.BlogProcesser;
import com.example.demo.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 博客爬取RestApi
 *
 * @author
 * @date 2021年8月8日11:02:09
 */
@RestController
@RequestMapping("/spider")
@Api(value = "博客爬取RestApi", tags = {"博客爬取相关接口"})
@Slf4j
public class BlogSpiderRestApi {

    @Autowired
    BlogCrawler blogCrawler;
    @Autowired
    BlogProcesser blogProcesser;
    @Autowired
    BlogPipeline blogPipeline;


    /**
     * 爬取csdn博客
     *
     * @return
     */
    @ApiOperation(value = "startSpiderCsdn", notes = "startSpiderCsdn")
    @RequestMapping(value = "/startSpiderCsdn", method = RequestMethod.GET)
    public String startSpiderCsdn() {
//        ConcurrentHashSet<BlogSpider> localData;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        blogPipeline.countDownLatch= countDownLatch;
        blogProcesser.clearData();
        blogCrawler.getSpider();
        blogCrawler.startCrawling();
        List<BlogElasticsearchModel> data = blogPipeline.getData();

        System.out.println(data.size());




        return ResultUtil.successWithDataAndMessage(data,"爬取");

    }

    /**
     * 爬取csdn博客
     *
     * @return
     */
    @ApiOperation(value = "stopSpiderCsdn", notes = "stopSpiderCsdn")
    @RequestMapping(value = "/stopSpiderCsdn", method = RequestMethod.GET)
    public String stopSpiderCsdn() {

        //关闭蜘蛛爬取内容
        blogCrawler.stopCrawling();
        return "爬虫结束";
    }
}

