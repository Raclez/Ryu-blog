package com.example.demo.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.demo.base.entity.SuperEntity;
import lombok.Data;

/**
 * <p>
 * 标签表
 * </p>
 *
 * @author Ryu
 * @since 2018-09-08
 */
@Data
@TableName("t_tag")
public class Tag extends SuperEntity<Tag> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签内容
     */
    private String content;

    /**
     * 标签简介
     */
    private int clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;
}
