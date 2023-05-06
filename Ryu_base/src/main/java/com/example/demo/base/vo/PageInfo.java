package com.example.demo.base.vo;

import com.example.demo.base.validator.Messages;
import com.example.demo.base.validator.annotion.LongNotNull;
import com.example.demo.base.validator.group.GetList;
import lombok.Data;

/**
 * PageVO  用于分页
 *
 * @author Ryu
 * @create:
 */
@Data
public class PageInfo<T> {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.PAGE_NOT_NULL)
    private Long currentPage;

    /**
     * 页大小
     */
    @LongNotNull(groups = {GetList.class}, message = Messages.SIZE_NOT_NULL)
    private Long pageSize;
}
