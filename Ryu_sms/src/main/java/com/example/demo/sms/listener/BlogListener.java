package com.example.demo.sms.listener;

import com.example.demo.commons.feign.SearchFeignClient;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.sms.global.RedisConf;
import com.example.demo.sms.global.SysConf;
import com.example.demo.utils.JsonUtils;
import com.example.demo.utils.RedisUtil;
import com.example.demo.base.global.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    RabbitTemplate rabbitTemplate;
   int MAX_RETRY_ATTEMPTS=2;
    @Resource
    private SearchFeignClient searchFeignClient;
    @Autowired
    ObjectMapper objectMapper;



    @RabbitListener(queues = "Ryu.blog")
    public void spider(BlogElasticsearchModel esMessage, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        log.info("Ryu-sms处理增加博客");
    try {

            String s = searchFeignClient.addEsblogToEs(esMessage);
//        BlogSpider blogSpider = new BlogSpider();
//        BeanUtils.copyProperties(esMessage, blogSpider);
//        blogSpiderService.save(blogSpider);
        if(s.equals("{\"message\":\"搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败\",\"code\":\"error\"}"))
                throw new Exception("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败zzzzzzz");
            channel.basicAck(deliveryTag,false);

        } catch (Exception e) {
                log.error("Reached maximum retry attempts for message. Further processing halted.");
                // 如果达到最大重试次数，不再重试，进行其他处理，比如记录到死信队列等
                // 这里你可以添加额外的逻辑来处理达到最大重试次数后的操作
                // 将消息发送到死信队列
                try {
                    channel.basicReject(deliveryTag, false);
                    rabbitTemplate.convertAndSend("exchange.spider", "Ryu.spider", esMessage);
//                    channel.basicAck(deliveryTag,  false);

                } catch (IOException ex) {
                    log.error("Failed to send message to dead letter queue. Exception: {}", ex.getMessage());
                }

        }



    // TODO 在这里同时需要对Redis和Solr进行操作，同时利用MQ来保证数据一致性
//    @RabbitListener(queues = "Ryu.blog")
//    public void updateRedis(Message esMessage, Channel channel) {
//        log.info("Ryu-sms处理增加博客");
//
//        try {
//            List<BlogElasticsearchModel> elasticsearchModels = objectMapper.readValue(esMessage.getBody(), new TypeReference<List<BlogElasticsearchModel>>(){});
//            String s = searchFeignClient.addEsblogsToEs(elasticsearchModels);
//            if(s.equals("{\"message\":\"搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败\",\"code\":\"error\"}"))
//                throw new Exception("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败zzzzzzz");
//            channel.basicAck(esMessage.getMessageProperties().getDeliveryTag(),false);
//
//        } catch (Exception e) {
////            log.error("Failed to process message. Exception: {}", e.getMessage());
////
////            // 获取消息的重试次数
////            int retryCount = esMessage.getMessageProperties().getHeader("retryCount") != null ?
////                    esMessage.getMessageProperties().getHeader("retryCount") : 0;
////                log.info("消息重试次数: {}", retryCount);
////            // 如果重试次数小于限制值，重新发送消息
////            if (retryCount < MAX_RETRY_ATTEMPTS) {
////                try {
////                    // 将重试次数+1，并重新发送消息
////                    esMessage.getMessageProperties().setHeader("retryCount",++retryCount);
////                    channel.basicNack(esMessage.getMessageProperties().getDeliveryTag(), false, true);
////                } catch (IOException ex) {
////                    log.error("Failed to requeue message. Exception: {}", ex.getMessage());
////                }
////            } else {
//                log.error("Reached maximum retry attempts for message. Further processing halted.");
//                // 如果达到最大重试次数，不再重试，进行其他处理，比如记录到死信队列等
//                // 这里你可以添加额外的逻辑来处理达到最大重试次数后的操作
//                // 将消息发送到死信队列
//                try {
//                    rabbitTemplate.convertAndSend("exchange.spider", "Ryu.spider", esMessage);
//                    channel.basicAck(esMessage.getMessageProperties().getDeliveryTag(),  false);
//
//                } catch (IOException ex) {
//                    log.error("Failed to send message to dead letter queue. Exception: {}", ex.getMessage());
//                }
////            }
//        }
//        if (esMessage != null) {
//            String operation = esMessage.getOperation();
//
//            //从Redis清空对应的数据
////            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_ONE);
////            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_TWO);
////            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_THREE);
////            redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_FOUR);
////            redisUtil.delete(RedisConf.HOT_BLOG);
////            redisUtil.delete(RedisConf.NEW_BLOG);
////            redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT);
////            redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
////            redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
//
//            switch (operation) {
//                case SysConf.DELETE_BATCH: {
//
//                    log.info("Ryu-sms处理批量删除博客");
//                    redisUtil.set(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON, "");
//                    redisUtil.set(RedisConf.MONTH_SET, "");
//
////                     删除ElasticSearch博客索引
////                    searchFeignClient.deleteElasticSearchByUids(uid);
//
//                    // 删除Solr博客索引
////                    searchFeignClient.deleteSolrIndexByUids(uid);
//                }
//                break;
//                case SysConf.EDIT_BATCH: {
//
//                    log.info("Ryu-sms处理批量编辑博客");
//                    redisUtil.set(RedisConf.BLOG_SORT_BY_MONTH + Constants.SYMBOL_COLON, "");
//                    redisUtil.set(RedisConf.MONTH_SET, "");
//
//                }
//                break;
//                case SysConf.ADD: {
//                    log.info("Ryu-sms处理增加博客");
//                    try {
//                        List<BlogElasticsearchModel> list = esMessage.getData();
//                        searchFeignClient.addEsblogsToEs(list);
////                        channel.basicAck(esMessage.getMessageProperties().getDeliveryTag(),false,true);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                break;
//
////                case SysConf.EDIT: {
////                    log.info("Ryu-sms处理编辑博客");
////                    updateSearch(map);
////
////                    // 更新ES索引
////                    searchFeignClient.addElasticSearchIndexByUid(uid);
////
////                    // 更新Solr索引
//////                    searchFeignClient.updateSolrIndexByUid(uid);
////                }
////                break;
////
////                case SysConf.DELETE: {
////                    log.info("Ryu-sms处理删除博客: uid:" + uid);
////                    updateSearch(map);
////
////                    // 删除ES索引
////                    searchFeignClient.deleteElasticSearchByUid(uid);
////
////                    // 删除Solr索引
//////                    searchFeignClient.deleteSolrIndexByUid(uid);
////                }
////                break;
//                default: {
//                    log.info("Ryu-sms处理博客");
//                }
//            }
//        }
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
