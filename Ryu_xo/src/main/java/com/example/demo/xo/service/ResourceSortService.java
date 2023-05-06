package com.example.demo.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.commons.entity.ResourceSort;
import com.example.demo.xo.vo.ResourceSortVO;
import com.example.demo.base.service.SuperService;

import java.util.List;

/**
 * 资源分类表 服务类
 *
 * @author
 * @date 2021-07-04
 */
public interface ResourceSortService extends SuperService<ResourceSort> {

    /**
     * 获取资源分类列表
     *
     * @param resourceSortVO
     * @return
     */
    public IPage<ResourceSort> getPageList(ResourceSortVO resourceSortVO);

    /**
     * 新增资源分类
     *
     * @param resourceSortVO
     */
    public String addResourceSort(ResourceSortVO resourceSortVO);

    /**
     * 编辑资源分类
     *
     * @param resourceSortVO
     */
    public String editResourceSort(ResourceSortVO resourceSortVO);

    /**
     * 批量删除资源分类
     *
     * @param resourceSortVOList
     */
    public String deleteBatchResourceSort(List<ResourceSortVO> resourceSortVOList);

    /**
     * 置顶资源分类
     *
     * @param resourceSortVO
     */
    public String stickResourceSort(ResourceSortVO resourceSortVO);
}
