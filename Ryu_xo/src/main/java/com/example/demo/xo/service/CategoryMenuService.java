package com.example.demo.xo.service;

import com.example.demo.base.service.SuperService;
import com.example.demo.commons.entity.CategoryMenu;
import com.example.demo.xo.vo.CategoryMenuVO;

import java.util.List;
import java.util.Map;

/**
 * 菜单表 服务类
 *
 * @author
 * @date 2021年11月23日10:41:47
 */
public interface CategoryMenuService extends SuperService<CategoryMenu> {

    /**
     * 获取菜单列表
     *
     * @param categoryMenuVO
     * @return
     */
    public Map<String, Object> getPageList(CategoryMenuVO categoryMenuVO);

    /**
     * 获取全部菜单列表
     *
     * @param keyword
     * @return
     */
    public List<CategoryMenu> getAllList(String keyword);

    /**
     * 获取所有二级菜单-按钮列表
     *
     * @param keyword
     * @return
     */
    public List<CategoryMenu> getButtonAllList(String keyword);

    /**
     * 新增菜单
     *
     * @param categoryMenuVO
     */
    public String addCategoryMenu(CategoryMenuVO categoryMenuVO);

    /**
     * 编辑菜单
     *
     * @param categoryMenuVO
     */
    public String editCategoryMenu(CategoryMenuVO categoryMenuVO);

    /**
     * 批量删除菜单
     *
     * @param categoryMenuVO
     */
    public String deleteCategoryMenu(CategoryMenuVO categoryMenuVO);

    /**
     * 置顶菜单
     *
     * @param categoryMenuVO
     */
    public String stickCategoryMenu(CategoryMenuVO categoryMenuVO);


    /**
     * 当前角色查询菜单
     * @param roleNames
     * @return
     */
    public List<CategoryMenu> getMenusByRole(List<String> roleNames);
}
