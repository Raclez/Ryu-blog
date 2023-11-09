package com.example.demo.search.restapi;

import com.example.demo.commons.entity.Blog;
import com.example.demo.commons.feign.WebFeignClient;
import com.example.demo.commons.pojo.BlogElasticsearchModel;
import com.example.demo.search.global.MessageConf;
import com.example.demo.search.global.SysConf;
import com.example.demo.search.repository.BlogRepository;
import com.example.demo.search.service.ElasticSearchService;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.StringUtils;
import com.example.demo.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ElasticSearch RestAPI
 *
 * @author Ryu
 * @create: 2021年1月15日16:26:16
 */
@RequestMapping("/search")
@Api(value = "ElasticSearch相关接口", tags = {"ElasticSearch相关接口"})
@RestController
public class ElasticSearchRestApi {
@Autowired
   ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    private ElasticSearchService searchService;
    @Autowired
    private BlogRepository blogRepository;
    @Resource
    private WebFeignClient webFeignClient;


    @ApiOperation(value = "通过ElasticSearch搜索博客", notes = "通过ElasticSearch搜索博客", response = String.class)
    @GetMapping("/elasticSearchBlog")
    public String searchBlog(HttpServletRequest request,
                             @RequestParam(required = false) String keywords,
                             @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer
                                     currentPage,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer
                                     pageSize) {

        if (StringUtils.isEmpty(keywords)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
//        return ResultUtil.result(SysConf.SUCCESS, searchService.search(keywords, currentPage, pageSize));
        return ResultUtil.result(SysConf.SUCCESS, searchService.searchByKey(keywords, currentPage, pageSize));

    }

    @ApiOperation(value = "通过uids删除ElasticSearch博客索引", notes = "通过uids删除ElasticSearch博客索引", response = String.class)
    @PostMapping("/deleteElasticSearchByUids")
    public String deleteElasticSearchByUids(@RequestParam(required = true) String uids) {

        List<String> uidList = StringUtils.changeStringToString(uids, SysConf.FILE_SEGMENTATION);

        for (String uid : uidList) {
            blogRepository.deleteById(uid);
        }

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "通过博客uid删除ElasticSearch博客索引", notes = "通过uid删除博客", response = String.class)
    @PostMapping("/deleteElasticSearchByUid")
    public String deleteElasticSearchByUid(@RequestParam(required = true) String uid) {
        blogRepository.deleteById(uid);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "ElasticSearch通过博客Uid添加索引", notes = "添加博客", response = String.class)
    @PostMapping("/addElasticSearchIndexByUid")
    public String addElasticSearchIndexByUid(@RequestParam(required = true) String uid) {

        String result = webFeignClient.getBlogByUid(uid);

        Blog eblog = WebUtils.getData(result, Blog.class);
        if (eblog == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INSERT_FAIL);
        }
        BlogElasticsearchModel blog = searchService.buidBlog(eblog);
        blogRepository.save(blog);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    @ApiOperation(value = "ElasticSearch初始化索引", notes = "ElasticSearch初始化索引", response = String.class)
    @PostMapping("/initElasticSearchIndex")
    public String initElasticSearchIndex() throws ParseException {
//        elasticsearchTemplate.deleteIndex(BlogElasticsearchModel.class);
        elasticsearchTemplate.createIndex(BlogElasticsearchModel.class);
        elasticsearchTemplate.putMapping(BlogElasticsearchModel.class);

//        Long page = 1L;
//        Long row = 10L;
//        Integer size = 0;
//
//        do {
//            // 查询blog信息
//            String result = webFeignClient.getBlogBySearch(page, row);
//
//            //构建blog
//            List<Blog> blogList = WebUtils.getList(result, Blog.class);
//            size = blogList.size();
//
//            List<BlogElasticsearchModel> blogElasticsearchModelList = blogList.stream()
//                    .map(searchService::buidBlog).collect(Collectors.toList());
//
//            //存入索引库
//            blogRepository.saveAll(blogElasticsearchModelList);
//            // 翻页
//            page++;
//        } while (size == 15);

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }
    @PostMapping("/addEsBlog")
    @ApiOperation(value = "ElasticSearch插入博客", notes = "ElasticSearch插入博客", response = String.class)
    public String addEsblogToEs(@RequestBody BlogElasticsearchModel blogElasticsearchModel){
        blogRepository.save(blogElasticsearchModel);
        return ResultUtil.result(SysConf.SUCCESS,MessageConf.INSERT_SUCCESS);
    }

    @PostMapping("/addEsBlogs")
    @ApiOperation(value = "ElasticSearch插入博客", notes = "ElasticSearch插入博客", response = String.class)
    public String addEsblogsToEs(@RequestBody List<BlogElasticsearchModel> blogElasticsearchModel){

        for (BlogElasticsearchModel elasticsearchModel : blogElasticsearchModel) {
            blogRepository.save(elasticsearchModel);
        }

//        blogRepository.saveAll(blogElasticsearchModel);
        return ResultUtil.result(SysConf.SUCCESS,MessageConf.INSERT_SUCCESS);
    }
}
