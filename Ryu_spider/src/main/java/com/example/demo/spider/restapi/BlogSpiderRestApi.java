package com.example.demo.spider.restapi;


import com.example.demo.spider.mapper.BlogSpiderMapper;
import com.example.demo.spider.pipeline.BlogPipeline;
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
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.management.JMException;

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
    private BlogProcesser blogProcesser;

    @Autowired
    private BlogPipeline blogPipeline;

    private Spider spider;
    @Autowired
    BlogSpiderMapper blogSpiderMapper;

    /**
     * 爬取csdn博客
     *
     * @return
     */
    @ApiOperation(value = "startSpiderCsdn", notes = "startSpiderCsdn")
    @RequestMapping(value = "/startSpiderCsdn", method = RequestMethod.GET)
    public String startSpiderCsdn() {

        if (spider != null) {
            spider.run();
        }
        //开启蜘蛛爬取内容
        spider = Spider.create(blogProcesser)
                .addUrl("https://www.csdn.net/")
                .addPipeline(blogPipeline)
                .setScheduler(new QueueScheduler())
                .thread(10)

                .setScheduler(new RedisScheduler(new JedisPool(new GenericObjectPoolConfig(), "ryu.asia", 6379, 50000, "475118582")));
//                .setDownloader(new HttpClientDownloader());

        try {
            SpiderMonitor.instance().register(spider);
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
        spider.start();

return ResultUtil.successWithMessage("爬取");

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
        spider.stop();

        return "关闭爬虫";
    }
}

