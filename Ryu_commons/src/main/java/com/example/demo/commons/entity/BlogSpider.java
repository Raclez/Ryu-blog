package com.example.demo.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.demo.base.entity.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件实体类
 *
 * @author  Ryu
 * @since 2021年7月8日19:46:41
 */
@TableName("t_blog_spider")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogSpider extends SuperEntity<BlogSpider> {

    private static final long serialVersionUID = 1L;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客简介
     * updateStrategy = FieldStrategy.IGNORED ：表示更新时候忽略非空判断
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;


    /**
     * 博客链接
     */
    private String link;

//    /**
//     * 博客内容
//     */
//    private String content;
//

}
