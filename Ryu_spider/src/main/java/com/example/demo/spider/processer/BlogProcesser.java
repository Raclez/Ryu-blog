package com.example.demo.spider.processer;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.commons.pojo.ESMessage;
import com.example.demo.spider.entity.BlogSpider;
import com.example.demo.spider.global.SysConf;
import com.example.demo.spider.service.BlogSpiderService;
import com.example.demo.utils.IdWorkerUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬取的博客页面处理
 *
 * @author
 * @date 2021年1月8日16:47:34
 */
@Component
public class BlogProcesser implements PageProcessor {

@Autowired
BlogCrawler blogCrawler;

    @Autowired
    private BlogSpiderService blogSpiderService;

    @Autowired
    LocalDataStorage localDataStorage;
    @Autowired
    RabbitTemplate rabbitTemplate;
    // 使用 AtomicInteger 来安全计数
    private AtomicInteger count = new AtomicInteger(0);
    public CountDownLatch countDownLatch;

    private int maxPageCount = 10; // 设置最大页面数量

    private int currentCount;
    private  IdWorkerUtils idWorkerUtils=new IdWorkerUtils();;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public  ConcurrentHashSet<BlogSpider> dataBuffer = new ConcurrentHashSet<>(); // 使用 BlockingQueue 来实现更细粒度的同步
    private  List<BlogElasticsearchModel> list = new CopyOnWriteArrayList<>(); // 创建一个缓冲列表用于暂存数据
    public ThreadLocal<ConcurrentHashSet<BlogSpider>> threadLocal = ThreadLocal.withInitial(ConcurrentHashSet::new);

    public    boolean isSpider=false;





    /**
     * 处理我们需要的页面
     */
    @Override
    public void process(Page page) {
        // 判断页面是博客列表页还是博客内容页
        if (page.getUrl().regex("https://blog.csdn.net/[a-zA-Z0-9_]+/article/details/[0-9]{9}").match()) {

            // 达到最大页面数量时停止爬取
            //TODO  不知道为什么出现线程问题先加锁解决
            int currentCount = count.incrementAndGet();

            if (currentCount> maxPageCount) {
                isSpider=true;
                blogCrawler.stopCrawling();

            }

            // 处理博客内容页
            String title = page.getHtml().xpath("//h1/text()").get();
            String content = page.getHtml().xpath("//div[@id='content_views']").get();
            String author = page.getHtml().xpath("//div[@class='blog-detail-avatar']/a/text()").get(); // 使用XPath提取作者信息
            String url = page.getUrl().toString();  // 获取页面的URL
            String id = String.valueOf(idWorkerUtils.nextId());

            BlogElasticsearchModel elasticsearchModel = new BlogElasticsearchModel();

            elasticsearchModel.setLink(url);
            elasticsearchModel.setTitle(title);
            elasticsearchModel.setContent(content);
            elasticsearchModel.setId( id);

            BlogSpider blogSpider = new BlogSpider();
            blogSpider.setLink(url);
            blogSpider.setTitle(title);
            blogSpider.setUid(id);
            page.putField("blogSpider", blogSpider);
            page.putField("elasticsearchModel", elasticsearchModel);
            page.putField("isSpider", isSpider);

            dataBuffer.add(blogSpider);
            list.add(elasticsearchModel);



        } else {
            // 处理博客列表页
            List<String> blogLinks = page.getHtml().regex("https://blog.csdn.net/[a-zA-Z0-9_]+/article/details/[0-9]{9}").all();
            for (String link : blogLinks) {
                // 添加博客内容链接到下一页的抓取队列
                page.addTargetRequest(link);
            }
        }

        }


    //站点信息设置
    @Override
    public Site getSite() {
        return Site.me().setCharset("utf8").setRetryTimes(2).setSleepTime(2000).setTimeOut(4000);
    }
    public void  sendEsMessage(ESMessage esMessage){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("exchange.direct", SysConf.Ryu_BLOG,esMessage);


    }

    public void clearData(){
        count.set(0);
        isSpider=false;
    }


}
