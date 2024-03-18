package com.example.demo.web.restapi;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.commons.entity.Blog;
import com.example.demo.commons.feign.PictureFeignClient;
import com.example.demo.utils.IpUtils;
import com.example.demo.utils.JsonUtils;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.StringUtils;
import com.example.demo.web.annotion.log.BussinessLog;
import com.example.demo.web.global.MessageConf;
import com.example.demo.web.global.SysConf;
import com.example.demo.xo.global.RedisConf;
import com.example.demo.xo.service.BlogService;
import com.example.demo.xo.utils.WebUtil;
import com.example.demo.base.enums.EBehavior;
import com.example.demo.base.enums.EPublish;
import com.example.demo.base.enums.EStatus;
import com.example.demo.base.global.Constants;
import com.example.demo.base.global.ECode;
import com.example.demo.base.holder.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 文章详情 RestApi
 *
 * @author
 * @date 2021-07-04
 */
@RestController
@RefreshScope
@RequestMapping("/content")
@Api(value = "文章详情相关接口", tags = {"文章详情相关接口"})
@Slf4j
public class BlogContentRestApi {
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private BlogService blogService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value(value = "${BLOG.ORIGINAL_TEMPLATE}")
    private String ORIGINAL_TEMPLATE;
    @Value(value = "${BLOG.REPRINTED_TEMPLATE}")
    private String REPRINTED_TEMPLATE;



    @BussinessLog(value = "点击博客", behavior = EBehavior.BLOG_CONTNET)
    @ApiOperation(value = "通过Uid获取博客内容", notes = "通过Uid获取博客内容")
    @GetMapping("/getBlogByUid")
    public String getBlogByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid,
                               @ApiParam(name = "oid", value = "博客OID", required = false) @RequestParam(name = "oid", required = false, defaultValue = "0") Integer oid) {

    HttpServletRequest request = RequestHolder.getRequest();
    String ip = IpUtils.getIpAddr(request);
    if (StringUtils.isEmpty(uid)&& oid <= 0) {
        return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
    }
    Blog blog;
        String oidString = oid.toString();
        // 从Redis中获取博客信息
    if(stringRedisTemplate.opsForHash().hasKey("BLOG_DETAIL", oidString)){
        String  blogJson= (String)stringRedisTemplate.opsForHash().get("BLOG_DETAIL", oidString);
        blog= JSON.parseObject(blogJson, Blog.class);
        return ResultUtil.result(SysConf.SUCCESS, blog);
    }else {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.OID, oid);
        blog = blogService.getOne(queryWrapper);
        if (blog == null || blog.getStatus() == EStatus.DISABLED || EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {
            return ResultUtil.result(ECode.ERROR, MessageConf.BLOG_IS_DELETE);
        }
    }

    // 设置文章版权申明
    CompletableFuture<Void> copyrightFuture = CompletableFuture.runAsync(() -> setBlogCopyright(blog));
    //设置博客标签
    CompletableFuture<Void> tagFuture = CompletableFuture.runAsync(() -> blogService.setTagByBlog(blog));

    //获取分类
    CompletableFuture<Void> sortFuture = CompletableFuture.runAsync(() -> blogService.setSortByBlog(blog));

    //设置博客标题图
    CompletableFuture<Void> photoFuture = CompletableFuture.runAsync(() -> setPhotoListByBlog(blog));

    // 等待所有异步任务完成
       CompletableFuture.allOf(copyrightFuture, tagFuture, sortFuture, photoFuture).join();
        stringRedisTemplate.opsForHash().put("BLOG_DETAIL", oidString, JSON.toJSONString(blog));
        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = stringRedisTemplate.opsForValue().get(RedisConf.BLOG_CLICK + ip + "#" + blog.getUid());

    if (StringUtils.isEmpty(jsonResult)) {
        //给博客点击数增加
        Integer clickCount = blog.getClickCount() + 1;
        blog.setClickCount(clickCount);

        //将该用户点击记录存储到redis中, 24小时后过期
        stringRedisTemplate.opsForValue().set(RedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + blog.getUid(), blog.getClickCount().toString(),
                24, TimeUnit.HOURS);

        //异步更新数据库
        CompletableFuture.runAsync(blog::updateById);
    }

    return ResultUtil.result(SysConf.SUCCESS, blog);
}
    @ApiOperation(value = "通过Uid获取博客点赞数", notes = "通过Uid获取博客点赞数")
    @GetMapping("/getBlogPraiseCountByUid")
    public String getBlogPraiseCountByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid) {

        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogPraiseCountByUid(uid));
    }

    @BussinessLog(value = "通过Uid给博客点赞", behavior = EBehavior.BLOG_PRAISE)
    @ApiOperation(value = "通过Uid给博客点赞", notes = "通过Uid给博客点赞")
    @GetMapping("/praiseBlogByUid")
    public String praiseBlogByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        return blogService.praiseBlogByUid(uid);
    }

    @ApiOperation(value = "根据标签Uid获取相关的博客", notes = "根据标签获取相关的博客")
    @GetMapping("/getSameBlogByTagUid")
    public String getSameBlogByTagUid(@ApiParam(name = "tagUid", value = "博客标签UID", required = true) @RequestParam(name = "tagUid", required = true) String tagUid,
                                      @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.getSameBlogByTagUid(tagUid));
    }

    @ApiOperation(value = "根据BlogUid获取相关的博客", notes = "根据BlogUid获取相关的博客")
    @GetMapping("/getSameBlogByBlogUid")
    public String getSameBlogByBlogUid(@ApiParam(name = "blogUid", value = "博客标签UID", required = true) @RequestParam(name = "blogUid", required = true) String blogUid) {
        if (StringUtils.isEmpty(blogUid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        List<Blog> blogList = blogService.getSameBlogByBlogUid(blogUid);
        IPage<Blog> pageList = new Page<>();
        pageList.setRecords(blogList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    /**
     * 设置博客标题图
     *
     * @param blog
     */
    private void setPhotoListByBlog(Blog blog) {
        //获取标题图片
        if (blog != null && !StringUtils.isEmpty(blog.getFileUid())) {
            String result = this.pictureFeignClient.getPicture(blog.getFileUid(), Constants.SYMBOL_COMMA);
            List<String> picList = webUtil.getPicture(result);
            if (picList != null && picList.size() > 0) {
                blog.setPhotoList(picList);
            }
        }
    }

    /**
     * 设置博客版权
     *
     * @param blog
     */
    private void setBlogCopyright(Blog blog) {

        //如果是原创的话
        if (Constants.STR_ONE.equals(blog.getIsOriginal())) {
            blog.setCopyright(ORIGINAL_TEMPLATE);
        } else {
            String reprintedTemplate = REPRINTED_TEMPLATE;
            String[] variable = {blog.getArticlesPart(), blog.getAuthor()};
            String str = String.format(reprintedTemplate, variable);
            blog.setCopyright(str);
        }
    }
}

