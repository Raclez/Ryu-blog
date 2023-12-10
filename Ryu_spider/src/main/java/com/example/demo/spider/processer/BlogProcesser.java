package com.example.demo.spider.processer;

import com.example.demo.commons.entity.BlogSpider;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.utils.IdWorkerUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    LocalDataStorage localDataStorage;
    @Autowired
    RabbitTemplate rabbitTemplate;
    // 使用 AtomicInteger 来安全计数
    private AtomicInteger count = new AtomicInteger(0);
    @Value("${spider.count}")
    private int maxPageCount;// 设置最大页面数量
    private  IdWorkerUtils idWorkerUtils=new IdWorkerUtils();;

    public AtomicBoolean isSpider=new AtomicBoolean(false);





    /**
     * 处理我们需要的页面
     */
    @Override
    public void process(Page page) {
        List<String> pageList = page.getHtml().regex("https://blog.csdn.net/[a-zA-Z0-9_]+/article/details/[0-9]{9}").all();

        // 处理博客内容页
        String title = page.getHtml().xpath("//h1/text()").get();
        String content = page.getHtml().xpath("//div[@id='content_views']").get();
        String author = page.getHtml().xpath("//div[@class='blog-detail-avatar']/a/text()").get(); // 使用XPath提取作者信息
        String url = page.getUrl().toString();  // 获取页面的URL
        String id = String.valueOf(idWorkerUtils.nextId());

            if(title!=null){
            // 达到最大页面数量时停止爬取
            //TODO  不知道为什么出现线程问题先加锁解决
            int currentCount = count.incrementAndGet();

            if (currentCount> maxPageCount) {
                isSpider.set(true);
                blogCrawler.stopCrawling();
            }



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

        }
        else {
           page.setSkip(true);

        }
        page.addTargetRequests(pageList); // 将列表页中提取到的链接加入待抓取队列
        }


    //站点信息设置
    @Override
    public Site getSite() {
        return Site.me().setCharset("utf8").setRetryTimes(2).setSleepTime(2000).setTimeOut(4000);
    }


    public void clearData(){
        count.set(0);
        isSpider.set(false);
    }


}
