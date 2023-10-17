package com.example.demo.sms.listener;

import com.example.demo.commons.feign.SearchFeignClient;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.commons.pojo.ESMessage;
import com.example.demo.sms.global.RedisConf;
import com.example.demo.sms.global.SysConf;
import com.example.demo.utils.JsonUtils;
import com.example.demo.utils.RedisUtil;
import com.example.demo.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 博客监听器【用于更新Redis和索引】
 *
 * @author 
 * @date 2021年11月3日下午12:53:23
 */
@Component
@Slf4j
public class BlogListener {

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private SearchFeignClient searchFeignClient;

    // TODO 在这里同时需要对Redis和Solr进行操作，同时利用MQ来保证数据一致性
    @RabbitListener(queues = "Ryu.blog")
    public void updateRedis(ESMessage esMessage) {

        if (esMessage != null) {
            String operation = esMessage.getOperation();

            //从Redis清空对应的数据
//            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_ONE);
//            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_TWO);
//            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_THREE);
//            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_FOUR);
//            redisUtil.delete(RedisConf.HOT_BLOG);
//            redisUtil.delete(RedisConf.NEW_BLOG);
//            redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT);
//            redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
//            redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);

            switch (operation) {
                case SysConf.DELETE_BATCH: {

                    log.info("Ryu-sms处理批量删除博客");
                    redisUtil.set(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON, "");
                    redisUtil.set(RedisConf.MONTH_SET, "");

//                     删除ElasticSearch博客索引
//                    searchFeignClient.deleteElasticSearchByUids(uid);

                    // 删除Solr博客索引
//                    searchFeignClient.deleteSolrIndexByUids(uid);
                }
                break;
                case SysConf.EDIT_BATCH: {

                    log.info("Ryu-sms处理批量编辑博客");
                    redisUtil.set(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON, "");
                    redisUtil.set(RedisConf.MONTH_SET, "");

                }
                break;
                case SysConf.ADD: {
                    log.info("Ryu-sms处理增加博客");
                    List<BlogElasticsearchModel> list = esMessage.getData();
//                    updateSearch(map);
                searchFeignClient.addEsblogsToEs(list);
                }
                break;

//                case SysConf.EDIT: {
//                    log.info("Ryu-sms处理编辑博客");
//                    updateSearch(map);
//
//                    // 更新ES索引
//                    searchFeignClient.addElasticSearchIndexByUid(uid);
//
//                    // 更新Solr索引
////                    searchFeignClient.updateSolrIndexByUid(uid);
//                }
//                break;
//
//                case SysConf.DELETE: {
//                    log.info("Ryu-sms处理删除博客: uid:" + uid);
//                    updateSearch(map);
//
//                    // 删除ES索引
//                    searchFeignClient.deleteElasticSearchByUid(uid);
//
//                    // 删除Solr索引
////                    searchFeignClient.deleteSolrIndexByUid(uid);
//                }
//                break;
                default: {
                    log.info("Ryu-sms处理博客");
                }
            }
        }
    }



    private void updateSearch(Map<String, String> map) {
        try {
            String level = map.get(SysConf.LEVEL);
            String createTime = map.get(SysConf.CREATE_TIME);
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(createTime))));
            String[] list = sd.split(Constants.SYMBOL_HYPHEN);
            String year = list[0];
            String month = list[1];
            String key = year + "年" + month + "月";
            redisUtil.delete(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON + key);
            String jsonResult = redisUtil.get(RedisConf.MONTH_SET);
            ArrayList<String> monthSet = (ArrayList<String>) JsonUtils.jsonArrayToArrayList(jsonResult);
            Boolean haveMonth = false;
            if (monthSet != null) {
                for (String item : monthSet) {
                    if (item.equals(key)) {
                        haveMonth = true;
                        break;
                    }
                }
                if (!haveMonth) {
                    monthSet.add(key);
                    redisUtil.set(RedisConf.MONTH_SET, JsonUtils.objectToJson(monthSet));
                }
            }

        } catch (Exception e) {
            log.error("更新Redis失败");
        }
    }
}
