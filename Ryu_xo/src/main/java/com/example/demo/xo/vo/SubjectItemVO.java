package com.example.demo.xo.vo;

import com.example.demo.base.validator.annotion.IntegerNotNull;
import com.example.demo.base.validator.annotion.NotBlank;
import com.example.demo.base.validator.group.Insert;
import com.example.demo.base.validator.group.Update;
import com.example.demo.base.vo.BaseVO;
import lombok.Data;

/**
 * SubjectItemVO
 *
 * @author Ryu
 * @create: 2021年8月22日21:53:40
 */
@Data
public class SubjectItemVO extends BaseVO<SubjectItemVO> {

    /**
     * 专题UID
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String subjectUid;

    /**
     * 博客UID
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String blogUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private int sort;

}
