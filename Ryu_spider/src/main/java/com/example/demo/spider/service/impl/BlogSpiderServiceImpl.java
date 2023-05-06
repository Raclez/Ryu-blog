package com.example.demo.spider.service.impl;


import com.example.demo.base.serviceImpl.SuperServiceImpl;
import com.example.demo.spider.entity.BlogSpider;
import com.example.demo.spider.mapper.BlogSpiderMapper;
import com.example.demo.spider.service.BlogSpiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客爬取服务实现类
 * </p>
 *
 * @author  Ryu
 * @since 2021年2月7日21:29:42
 */
@Slf4j
@Service
public class BlogSpiderServiceImpl extends SuperServiceImpl<BlogSpiderMapper, BlogSpider> implements BlogSpiderService {

}
