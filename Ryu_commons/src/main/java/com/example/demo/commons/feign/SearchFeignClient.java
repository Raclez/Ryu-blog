package com.example.demo.commons.feign;

import com.example.demo.commons.config.feign.FeignConfiguration;
import com.example.demo.commons.fallback.SearchFeignFallback;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 搜索服务feign远程调用
 *
 * @author
 * @date 2021年10月6日09:08:13
 */
@FeignClient(name = "Ryu-search", configuration = FeignConfiguration.class, fallback = SearchFeignFallback.class)
public interface SearchFeignClient {


    /**
     * 通过博客uid删除ElasticSearch博客索引
     *
     * @param uid
     * @return
     */
    @PostMapping("/search/deleteElasticSearchByUid")
    public String deleteElasticSearchByUid(@RequestParam(required = true) String uid);

    /**
     * 通过uids删除ElasticSearch博客索引
     *
     * @param uids
     * @return
     */
    @PostMapping("/search/deleteElasticSearchByUids")
    public String deleteElasticSearchByUids(@RequestParam(required = true) String uids);

    /**
     * 初始化ElasticSearch索引
     *
     * @return
     */
    @PostMapping("/search/initElasticSearchIndex")
    public String initElasticSearchIndex();

    /**
     * 通过uid来增加ElasticSearch索引
     *
     * @return
     */
    @PostMapping("/search/addElasticSearchIndexByUid")
    public String addElasticSearchIndexByUid(@RequestParam(required = true) String uid);


    /**
     * 通过博客uid删除Solr博客索引
     *
     * @param uid
     * @return
     */
    @PostMapping("/search/deleteSolrIndexByUid")
    public String deleteSolrIndexByUid(@RequestParam(required = true) String uid);

    /**
     * 通过uids删除Solr博客索引
     *
     * @param uids
     * @return
     */
    @PostMapping("/search/deleteSolrIndexByUids")
    public String deleteSolrIndexByUids(@RequestParam(required = true) String uids);

    /**
     * 初始化Solr索引
     *
     * @return
     */
    @PostMapping("/search/initSolrIndex")
    public String initSolrIndex();

    /**
     * 通过uid来增加Solr索引
     *
     * @return
     */
    @PostMapping("/search/addSolrIndexByUid")
    public String addSolrIndexByUid(@RequestParam(required = true) String uid);

    /**
     * 通过uid来更新Solr索引
     *
     * @return
     */
    @PostMapping("/search/updateSolrIndexByUid")
    public String updateSolrIndexByUid(@RequestParam(required = true) String uid);
    @PostMapping("/search/addEsBlog")
    public String addEsblogToEs(@RequestBody(required = true) BlogElasticsearchModel blogElasticsearchModel);
    @PostMapping("/search/addEsBlogs")

    public String addEsblogsToEs(@RequestBody(required = true) List<BlogElasticsearchModel> blogElasticsearchModel);
}
