package com.example.demo.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.demo.base.entity.SuperEntity;
import lombok.Data;

/**
 * 文件分类
 *
 * @author
 * @since
 */
@TableName("t_file_sort")
@Data
public class FileSort extends SuperEntity<FileSort> {

    private static final long serialVersionUID = 1L;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 模块分类名
     */
    private String sortName;

    /**
     * 存储路径
     */
    private String url;
}
