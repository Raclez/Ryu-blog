package com.example.demo.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.commons.entity.PictureSort;
import com.example.demo.xo.vo.PictureSortVO;
import com.example.demo.base.service.SuperService;

/**
 * 图片分类表 服务类
 *
 * @author
 * @date 2021-07-04
 */
public interface PictureSortService extends SuperService<PictureSort> {

    /**
     * 获取图片分类列表
     *
     * @param pictureSortVO
     * @return
     */
    public IPage<PictureSort> getPageList(PictureSortVO pictureSortVO);

    /**
     * 新增图片分类
     *
     * @param pictureSortVO
     */
    public String addPictureSort(PictureSortVO pictureSortVO);

    /**
     * 编辑图片分类
     *
     * @param pictureSortVO
     */
    public String editPictureSort(PictureSortVO pictureSortVO);

    /**
     * 删除图片分类
     *
     * @param pictureSortVO
     */
    public String deletePictureSort(PictureSortVO pictureSortVO);

    /**
     * 置顶图片分类
     *
     * @param pictureSortVO
     */
    public String stickPictureSort(PictureSortVO pictureSortVO);
}
