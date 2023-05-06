package com.example.demo.xo.vo;

import com.example.demo.base.validator.annotion.NotBlank;
import com.example.demo.base.validator.group.Insert;
import com.example.demo.base.validator.group.Update;
import com.example.demo.base.vo.BaseVO;
import lombok.Data;

/**
 * LinkVO
 *
 * @author Ryu
 * @create: 2019年12月11日12:41:28
 */
@Data
public class LinkVO extends BaseVO<LinkVO> {

    /**
     * 友链标题
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String title;
    /**
     * 友链介绍
     */
    private String summary;
    /**
     * 友链地址
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String url;

    /**
     * 友链状态： 0 申请中， 1：已上线，  2：已拒绝
     */
    private Integer linkStatus;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 站长邮箱
     */
    private String email;

    /**
     * 网站图标uid
     */
    private String fileUid;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;


    /**
     * 无参构造方法，初始化默认值
     */
    LinkVO() {

    }

}
