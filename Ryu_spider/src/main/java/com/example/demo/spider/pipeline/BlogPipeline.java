package com.example.demo.spider.pipeline;

import com.example.demo.base.enums.EPublish;
import com.example.demo.base.enums.EStatus;
import com.example.demo.commons.feign.SearchFeignClient;
import com.example.demo.commons.pojo.ESBlogIndex;
import com.example.demo.spider.entity.BlogSpider;
import com.example.demo.spider.global.SysConf;
import com.example.demo.spider.mapper.BlogSpiderMapper;
import com.example.demo.spider.util.IdWorker;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


    @Override
    public void process(ResultItems res, Task task) {
        //获取title和content
        String title = res.get("title");
        String content = res.get("content");
        System.out.println("title: " + title);
//        System.out.println("content: " + content);
        BlogSpider blog = null;
        ESBlogIndex blogIndex=null;
        if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(content)) {

            try {
                blog = new BlogSpider();
                blog.setUid(idWorker.nextId() + "");
                blog.setTitle(title);
                blog.setSummary(title);
                blog.setContent(content);
                blog.setTagUid("5c4c541e600ff422ccb371ee788f59d6");
                blog.setClickCount(0);
                blog.setCollectCount(0);
                blog.setStatus(EStatus.ENABLE);
                blog.setAdminUid("1f01cd1d2f474743b241d74008b12333");
                blog.setAuthor("Ryu");
                blog.setArticlesPart("Ryu博客");
                blog.setBlogSortUid("6a1c7a50c0e7b8e8657949bf02d5d0ca");
                blog.setLevel(0);
                blog.setIsPublish(EPublish.PUBLISH);
                blog.setSort(0);
            blogSpiderMapper.insert(blog);
                HashMap<String, String> map = new HashMap<>();
                map.put("command","add");
                map.put("blogUid",blog.getUid());
                map.put("title",title);
                map.put("content",content);
               sendEsMessage(map);



//                blogIndex = new ESBlogIndex();
//                blogIndex.setTitle(title);
//                blogIndex.setContent(content);
//                blogIndex.setSummary(title);
//                blogIndex.setUid(idWorker.toString());
//            searchFeignClient.addEsblogToEs(blogIndex);

                //下载到本地
//                DownloadUtil.download(title,"1",SAVE_PATH);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    public void  sendEsMessage(HashMap<String,String> map){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("exchange.direct", SysConf.Ryu_BLOG,map);


    }

}
