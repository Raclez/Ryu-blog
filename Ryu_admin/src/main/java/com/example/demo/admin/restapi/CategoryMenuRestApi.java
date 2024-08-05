package com.example.demo.admin.restapi;


import com.example.demo.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.example.demo.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.example.demo.admin.annotion.OperationLogger.OperationLogger;
import com.example.demo.commons.entity.CategoryMenu;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.SnowflakeIdWorker;
import com.example.demo.xo.service.CategoryMenuService;
import com.example.demo.xo.vo.CategoryMenuVO;
import com.example.demo.base.exception.ThrowableUtils;
import com.example.demo.base.validator.group.Delete;
import com.example.demo.base.validator.group.GetList;
import com.example.demo.base.validator.group.Insert;
import com.example.demo.base.validator.group.Update;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 菜单表 RestApi
 *
 * @author
 * @date 2021年6月27日15:45:18
 */

@RestController
@RequestMapping("/categoryMenu")
@Api(value = "菜单信息相关接口", tags = {"菜单信息相关接口"})
@Slf4j
public class CategoryMenuRestApi {

    @Autowired
    CategoryMenuService categoryMenuService;

    @AuthorityVerify
    @ApiOperation(value = "获取菜单列表", notes = "获取菜单列表", response = String.class)
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public String getList(@Validated({GetList.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(categoryMenuService.getPageList(categoryMenuVO));
    }

    @ApiOperation(value = "获取所有菜单列表", notes = "获取所有列表", response = String.class)
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public String getAll(@RequestParam(value = "keyword", required = false) String keyword) {
        return ResultUtil.successWithData(categoryMenuService.getAllList(keyword));
    }

    @ApiOperation(value = "获取所有二级菜单-按钮列表", notes = "获取所有二级菜单-按钮列表", response = String.class)
    @RequestMapping(value = "/getButtonAll", method = RequestMethod.GET)
    public String getButtonAll(@RequestParam(value = "keyword", required = false) String keyword) {

        return ResultUtil.successWithData(categoryMenuService.getButtonAllList(keyword));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加菜单")
    @ApiOperation(value = "增加菜单", notes = "增加菜单", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.addCategoryMenu(categoryMenuVO);
    }

    @AuthorityVerify
    @ApiOperation(value = "编辑菜单", notes = "编辑菜单", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.editCategoryMenu(categoryMenuVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除菜单")
    @ApiOperation(value = "删除菜单", notes = "删除菜单", response = String.class)
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.deleteCategoryMenu(categoryMenuVO);
    }

    /**
     * 如果是一级菜单，直接置顶在最前面，二级菜单，就在一级菜单内置顶
     *
     * @author xzx19950624@qq.com
     * @date 2021年11月29日上午9:22:59
     */
    @AuthorityVerify
    @OperationLogger(value = "置顶菜单")
    @ApiOperation(value = "置顶菜单", notes = "置顶菜单", response = String.class)
    @PostMapping("/stick")
    public String stick(@Validated({Delete.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.stickCategoryMenu(categoryMenuVO);
    }
//    public static void main(String[] args) {
//        File file = new File("/Users/ryu/Desktop/json"); // 你的 JSON 文件路径
//        File outputFile = new File("/Users/ryu/Desktop/output.json");
//
//
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<CategoryMenu> menus = objectMapper.readValue(file, new TypeReference<List<CategoryMenu>>() {
//            });
//
//            SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);// 初始化 Snowflake ID 生成器
//
//            // 现在你可以使用 menus 对象来处理菜单数据了
//            for (CategoryMenu menu : menus) {
//                long newUid = snowflakeIdWorker.nextId(); // 生成新的 UID
//                menu.setUid(String.valueOf(newUid)); // 设置新的 UID
//                // 更新子菜单的父 UID
//                List<CategoryMenu> childMenus = menu.getChildCategoryMenu();
//                if (childMenus != null && !childMenus.isEmpty()) {
//                    for (CategoryMenu childMenu : childMenus) {
//                        childMenu.setParentUid(String.valueOf(newUid)); // 设置父 UID
//                        long newChildUid = snowflakeIdWorker.nextId(); // 生成子菜单的新 UID
//                        childMenu.setUid(String.valueOf(newChildUid)); // 设置子菜单的新 UID
//                        if (childMenu.getChildCategoryMenu() != null && !childMenu.getChildCategoryMenu().isEmpty()) {
//                            for (CategoryMenu childChildMenu : childMenu.getChildCategoryMenu()) {
//                                long newChildChildUid = snowflakeIdWorker.nextId(); // 生成子菜单的新 UID
//                                childChildMenu.setUid(String.valueOf(newChildChildUid)); // 设置子菜单的新 UID
//                                childChildMenu.setParentUid(String.valueOf(newChildUid));
//                            }
//                        }
//                    }
//                }
//                // 处理菜单数据
//                System.out.println("Menu Name: " + menu.getName()+""+menu.getUid());
//                // 其他属性...
//                if (childMenus != null && !childMenus.isEmpty()) {
//                    for (CategoryMenu childMenu : childMenus) {
//                        // 处理子菜单...
//                        System.out.println("Child Menu Name: " + childMenu.getName()+""+childMenu.getUid());
//                        if (childMenu.getChildCategoryMenu() != null && !childMenu.getChildCategoryMenu().isEmpty()) {
//                            for (CategoryMenu childCategoryMenu : childMenu.getChildCategoryMenu()) {
//                                System.out.println(childCategoryMenu.getName()+"    "+childCategoryMenu.getUid());
//                            }
//                        }
//                        // 其他属性...
//                    }
//                }
//            }
//            objectMapper.writeValue(outputFile, menus);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}