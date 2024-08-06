package com.example.demo.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.base.enums.EMenuType;
import com.example.demo.base.enums.EStatus;
import com.example.demo.base.global.Constants;
import com.example.demo.base.serviceImpl.SuperServiceImpl;
import com.example.demo.commons.entity.CategoryMenu;
import com.example.demo.commons.entity.Role;
import com.example.demo.utils.RedisUtil;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.StringUtils;
import com.example.demo.xo.global.MessageConf;
import com.example.demo.xo.global.RedisConf;
import com.example.demo.xo.global.SQLConf;
import com.example.demo.xo.global.SysConf;
import com.example.demo.xo.mapper.CategoryMenuMapper;
import com.example.demo.xo.service.CategoryMenuService;
import com.example.demo.xo.service.RoleService;
import com.example.demo.xo.vo.CategoryMenuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author Ryu
 * @since 2021年11月23日10:42:30
 */
@Service
public class CategoryMenuServiceImpl extends SuperServiceImpl<CategoryMenuMapper, CategoryMenu> implements CategoryMenuService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CategoryMenuService categoryMenuService;
    @Autowired
    RoleService roleService;

    @Override
    public Map<String, Object> getPageList(CategoryMenuVO categoryMenuVO) {
        Map<String, Object> resultMap = new HashMap<>();
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(categoryMenuVO.getKeyword()) && !StringUtils.isEmpty(categoryMenuVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.NAME, categoryMenuVO.getKeyword().trim());
        }

        if (categoryMenuVO.getMenuLevel() != 0) {
            queryWrapper.eq(SQLConf.MENU_LEVEL, categoryMenuVO.getMenuLevel());
        }

        Page<CategoryMenu> page = new Page<>();
        page.setCurrent(categoryMenuVO.getCurrentPage());
        page.setSize(categoryMenuVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        IPage<CategoryMenu> pageList = categoryMenuService.page(page, queryWrapper);
        List<CategoryMenu> list = pageList.getRecords();

        List<String> ids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getParentUid())) {
                ids.add(item.getParentUid());
            }
        });

        if (ids.size() > 0) {
            Collection<CategoryMenu> parentList = categoryMenuService.listByIds(ids);
            Map<String, CategoryMenu> map = new HashMap<>();
            parentList.forEach(item -> {
                map.put(item.getUid(), item);
            });

            list.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getParentUid())) {
                    item.setParentCategoryMenu(map.get(item.getParentUid()));
                }
            });

            resultMap.put(SysConf.OTHER_DATA, parentList);
        }
        pageList.setRecords(list);
        resultMap.put(SysConf.DATA, pageList);
        return resultMap;
    }
//    @Override
//    public List<CategoryMenu> getAllList(String keyword) {
//        CompletableFuture<List<CategoryMenu>> future1 = CompletableFuture.supplyAsync(() -> {
//            QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq(SQLConf.MENU_LEVEL, "1");
//            if (StringUtils.isNotEmpty(keyword)) {
//                queryWrapper.eq(SQLConf.UID, keyword);
//            }
//            queryWrapper.orderByDesc(SQLConf.SORT);
//            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
//            queryWrapper.eq(SQLConf.MENU_TYPE, EMenuType.MENU);
//            List<CategoryMenu> list = categoryMenuService.list(queryWrapper);
//            return  list;
//        });
//        CompletableFuture<List<CategoryMenu>> future2 = CompletableFuture.supplyAsync(() -> {
//            QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq(SQLConf.MENU_LEVEL, "2");
//            if (StringUtils.isNotEmpty(keyword)) {
//                queryWrapper.eq(SQLConf.UID, keyword);
//            }
//            queryWrapper.orderByDesc(SQLConf.SORT);
//            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
//            List<CategoryMenu> list = categoryMenuService.list(queryWrapper);
//            return list;
//        });
//        CompletableFuture<List<CategoryMenu>> future3 = CompletableFuture.supplyAsync(() -> {
//            QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq(SQLConf.MENU_LEVEL, "3");
//            if (StringUtils.isNotEmpty(keyword)) {
//                queryWrapper.eq(SQLConf.UID, keyword);
//            }
//            queryWrapper.orderByDesc(SQLConf.SORT);
//            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
//            List<CategoryMenu> list = categoryMenuService.list(queryWrapper);
//            return list;
//        });
//        List<CategoryMenu> list = null;
//        List<CategoryMenu> list1 = null;
//        List<CategoryMenu> list2 = null;
//        try {
//            list = future1.get();
//            list1 = future2.get();
//            list2 = future3.get();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        for (CategoryMenu categoryMenu : list1) {
//            ArrayList<CategoryMenu> menuArrayList = new ArrayList<>();
//            for (CategoryMenu menu : list2) {
//                if(menu.getParentUid().equals(categoryMenu.getUid())){
//                    menuArrayList.add(menu);
//                }
//            }
//            Collections.sort(menuArrayList, new Comparator<CategoryMenu>() {
//
//                /*
//                 * int compare(CategoryMenu p1, CategoryMenu p2) 返回一个基本类型的整型，
//                 * 返回负数表示：p1 小于p2，
//                 * 返回0 表示：p1和p2相等，
//                 * 返回正数表示：p1大于p2
//                 */
//                @Override
//                public int compare(CategoryMenu o1, CategoryMenu o2) {
//
//                    //按照CategoryMenu的Sort进行降序排列
//                    if (o1.getSort() > o2.getSort()) {
//                        return -1;
//                    }
//                    if (o1.getSort().equals(o2.getSort())) {
//                        return 0;
//                    }
//                    return 1;
//                }
//
//            });
//            categoryMenu.setChildCategoryMenu(menuArrayList);
//        }
//
//        for (CategoryMenu parentItem : list) {
//            List<CategoryMenu> tempList = new ArrayList<>();
//            for (CategoryMenu item : list1) {
//
//                if (item.getParentUid().equals(parentItem.getUid())) {
//                    tempList.add(item);
//                }
//            }
//            Collections.sort(tempList);
//            parentItem.setChildCategoryMenu(tempList);
//        }
//
//
//        return list;
//    }

//    @Override
    public List<CategoryMenu> getAllList(String keyword) {
        List<CategoryMenu> categoryMenus = this.baseMapper.selectList(null);

        List<CategoryMenu>  categoryMenuList= buildCategoryMenus(categoryMenus);
        return categoryMenuList;
    }
    private List<CategoryMenu> getChildrenMenus(CategoryMenu category, List<CategoryMenu> all) {
        return all.stream()
                .filter(category1 -> category1.getParentUid().equals(category.getUid()))
                .sorted((s1,s2)->s2.getSort()-s1.getSort())
                .map(category1 -> {
                    category1.setChildCategoryMenu(getChildrenMenus(category1, all));
                    return category1;
                })
                .collect(Collectors.toList());
    }
   public List<CategoryMenu> getMenusByRole(List<String> roleNames){
       if (roleNames == null || roleNames.isEmpty()) {
           return Collections.emptyList();
       }

       List<CategoryMenu> allCategoryMenus = new ArrayList<>();

       for (String roleName : roleNames) {
           Role role = roleService.getOne(new QueryWrapper<Role>().eq("role_name", roleName));

           if (role == null || role.getCategoryMenuUids() == null) {
               continue;
           }

           String[] split = role.getCategoryMenuUids().replace("[", "").replace("]", "")
                   .replace("\"", "").split(",");
           List<String> menuIds = Arrays.asList(split);

           // 查询菜单信息
           List<CategoryMenu> categoryMenus = this.baseMapper.selectBatchIds(menuIds);
           // 构建菜单树结构
           List<CategoryMenu> buildCategoryMenus = buildCategoryMenus(categoryMenus);

           allCategoryMenus.addAll(buildCategoryMenus);
       }

       // 移除重复的菜单项（如果需要）
       return allCategoryMenus.stream()
               .distinct()
               .collect(Collectors.toList());

    }

    @Override
    public Map<String, Object> getMenusByUser(List<String> roleName) {

        List<String> categoryMenuUids=new ArrayList<>();
        roleName.stream().forEach(role->{
            Role role1 = roleService.getOne(new QueryWrapper<Role>().eq("role_name", role));
            List<String> stringList = Arrays.stream(role1.getCategoryMenuUids()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",")).collect(Collectors.toList());
            categoryMenuUids.addAll(stringList);
        });


        Collection<CategoryMenu> categoryMenuList = categoryMenuService.listByIds(categoryMenuUids);

        Set<String> secondMenuUidList = categoryMenuList.stream()
                .filter(item -> item.getMenuLevel() == SysConf.TWO && item.getMenuType() == EMenuType.MENU)
                .map(CategoryMenu::getUid)
                .collect(Collectors.toSet());

        List<CategoryMenu> buttonList = categoryMenuList.stream()
                .filter(item -> item.getMenuType() == EMenuType.BUTTON && StringUtils.isNotEmpty(item.getParentUid()))
                .peek(item -> secondMenuUidList.add(item.getParentUid()))
                .collect(Collectors.toList());

        Collection<CategoryMenu> childCategoryMenuList = categoryMenuService.listByIds(secondMenuUidList);

        List<String> parentCategoryMenuUids = childCategoryMenuList.stream()
                .filter(item -> item.getMenuLevel() == SysConf.TWO && StringUtils.isNotEmpty(item.getParentUid()))
                .map(CategoryMenu::getParentUid)
                .distinct()
                .collect(Collectors.toList());

        Collection<CategoryMenu> parentCategoryMenuList = categoryMenuService.listByIds(parentCategoryMenuUids);

        List<CategoryMenu> sortedParentList = new ArrayList<>(parentCategoryMenuList);
        sortedParentList.sort(Comparator.comparing(CategoryMenu::getSort).reversed()); // 使用比较器进行排序

        Map<String, Object> map = new HashMap<>();
        map.put(SysConf.PARENT_LIST, sortedParentList);
        map.put(SysConf.SON_LIST, childCategoryMenuList);
        map.put(SysConf.BUTTON_LIST, buttonList);

        return map;



    }

    private List<CategoryMenu> buildCategoryMenus(List<CategoryMenu> categoryMenus) {
        // 构建一级菜单
        return categoryMenus.stream()
                .filter(item -> item.getParentUid() .equals("0"))
                .map(categoryMenu -> {
                    categoryMenu.setChildCategoryMenu(getChildrenMenus(categoryMenu, categoryMenus));
                    return categoryMenu;
                })
                .sorted((s1,s2)->s2.getSort()-s1.getSort())
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryMenu> getButtonAllList(String keyword) {
        // 创建主查询条件
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.MENU_LEVEL, "2")
                .orderByDesc(SQLConf.SORT)
                .eq(SQLConf.STATUS, EStatus.ENABLE)
                .eq(SQLConf.MENU_TYPE, EMenuType.MENU);
        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.eq(SQLConf.UID, keyword);
        }

        // 获取符合条件的二级菜单
        List<CategoryMenu> firstLevelMenus = categoryMenuService.list(queryWrapper);

        // 获取所有二级菜单的UID
        List<String> firstLevelUids = firstLevelMenus.stream()
                .map(CategoryMenu::getUid)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());

        if (firstLevelUids.isEmpty()) {
            return firstLevelMenus; // 如果没有符合条件的二级菜单，直接返回
        }

        // 创建子菜单查询条件
        QueryWrapper<CategoryMenu> childWrapper = new QueryWrapper<>();
        childWrapper.in(SQLConf.PARENT_UID, firstLevelUids)
                .eq(SQLConf.STATUS, EStatus.ENABLE);

        // 获取子菜单
        List<CategoryMenu> childMenus = categoryMenuService.list(childWrapper);

        // 将子菜单按照父UID分组
        Map<String, List<CategoryMenu>> childMap = childMenus.stream()
                .filter(child -> StringUtils.isNotEmpty(child.getParentUid()))
                .collect(Collectors.groupingBy(CategoryMenu::getParentUid));

        // 过滤一级菜单，只保留存在子菜单的一级菜单
        List<CategoryMenu> secondLevelMenus = firstLevelMenus.stream()
                .filter(menu -> childMap.containsKey(menu.getUid()))
                .collect(Collectors.toList());

        // 设置子菜单并排序
        secondLevelMenus.forEach(menu -> {
            List<CategoryMenu> children = childMap.get(menu.getUid());
            if (children != null) {
                children.sort(Comparator.comparing(CategoryMenu::getSort));
                menu.setChildCategoryMenu(children);
            }
        });

        return secondLevelMenus;
    }


    @Override
    public String addCategoryMenu(CategoryMenuVO categoryMenuVO) {
        //如果是一级菜单，将父ID清空
        if (categoryMenuVO.getMenuLevel() == 1) {
            categoryMenuVO.setParentUid("0");
        }
        CategoryMenu categoryMenu = new CategoryMenu();
        BeanUtils.copyProperties(categoryMenuVO,categoryMenu);
        categoryMenu.setUpdateTime(new Date());
        categoryMenu.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editCategoryMenu(CategoryMenuVO categoryMenuVO) {
        CategoryMenu categoryMenu = new CategoryMenu();
        BeanUtils.copyProperties(categoryMenuVO,categoryMenu);
        categoryMenu.setUpdateTime(new Date());
        this.baseMapper.updateById(categoryMenu);
        // 修改成功后，需要删除redis中所有的admin访问路径
//        deleteAdminVisitUrl();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteCategoryMenu(CategoryMenuVO categoryMenuVO) {
            this.baseMapper.deleteById(categoryMenuVO.getUid());
        //TODO 修改菜单后管理员访问路径改变？
//        deleteAdminVisitUrl();
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String stickCategoryMenu(CategoryMenuVO categoryMenuVO) {
        // 构建查询条件
        QueryWrapper<CategoryMenu> queryWrapper = new QueryWrapper<>();
        Integer menuLevel = categoryMenuVO.getMenuLevel();
        Integer menuType = categoryMenuVO.getMenuType();

        // 如果是二级菜单 或者 按钮，就在当前的兄弟中，找出最大的一个
        if (menuLevel == Constants.NUM_TWO || menuType == EMenuType.BUTTON) {
            queryWrapper.eq(SQLConf.PARENT_UID, categoryMenuVO.getParentUid());
        }
        queryWrapper.eq(SQLConf.MENU_LEVEL, menuLevel);
        queryWrapper.orderByDesc(SQLConf.SORT);

        // 查找最大的排序值
        CategoryMenu maxSort = categoryMenuService.getOne(queryWrapper);

        // 处理查找结果
        if (maxSort == null || StringUtils.isEmpty(maxSort.getUid())) {
            return ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }

        // 更新排序值
        Integer sortCount = Optional.ofNullable(maxSort.getSort()).orElse(0) + 1;
        CategoryMenu categoryMenu = new CategoryMenu();
        BeanUtils.copyProperties(categoryMenuVO,categoryMenu);
        categoryMenu.setSort(sortCount);
        categoryMenu.setUpdateTime(new Date());
        categoryMenuService.updateById(categoryMenu);

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }


    /**
     * 删除Redis中管理员的访问路径
     */
    private void deleteAdminVisitUrl() {
        Set<String> keys = redisUtil.keys(RedisConf.ADMIN_VISIT_MENU + "*");
        redisUtil.delete(keys);
    }
}
