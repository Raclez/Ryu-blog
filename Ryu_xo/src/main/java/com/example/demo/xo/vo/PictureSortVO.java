package com.example.demo.xo.vo;

import com.example.demo.base.validator.annotion.IntegerNotNull;
import com.example.demo.base.validator.group.Insert;
import com.example.demo.base.validator.group.Update;
import com.example.demo.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * 相册分类实体类
 *
 * @author
 * @date 2021年7月17日16:10:38
 */
@ToString
@Data
public class PictureSortVO extends BaseVO<PictureSortVO> {

    /**
     * 父UID
     */
    private String parentUid;

    /**
     * 分类名
     */
    private String name;

    /**
     * 分类图片Uid
     */
    private String fileUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;

    /**
     * 是否显示  1: 是  0: 否
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer isShow;
}
