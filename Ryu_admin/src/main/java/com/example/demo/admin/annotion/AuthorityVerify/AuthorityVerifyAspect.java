package com.example.demo.admin.annotion.AuthorityVerify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.internal.LinkedTreeMap;
import com.example.demo.admin.global.MessageConf;
import com.example.demo.admin.global.RedisConf;
import com.example.demo.admin.global.SQLConf;
import com.example.demo.admin.global.SysConf;
import com.example.demo.commons.entity.Admin;
import com.example.demo.commons.entity.CategoryMenu;
import com.example.demo.commons.entity.Role;
import com.example.demo.utils.JsonUtils;
import com.example.demo.utils.RedisUtil;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.StringUtils;
import com.example.demo.xo.service.AdminService;
import com.example.demo.xo.service.CategoryMenuService;
import com.example.demo.xo.service.RoleService;
import com.example.demo.base.enums.EMenuType;
import com.example.demo.base.enums.EStatus;
import com.example.demo.base.global.ECode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限校验 切面实现
 *
 * @author:Ryu
 * @create: 2
 */
@Aspect
@Component
@Slf4j
public class AuthorityVerifyAspect {

    @Autowired
    CategoryMenuService categoryMenuService;

    @Autowired
    RoleService roleService;

    @Autowired
    AdminService adminService;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Pointcut(value = "@annotation(authorityVerify)")
    public void pointcut(com.example.demo.admin.annotion.AuthorityVerify.AuthorityVerify authorityVerify) {

    }

    @Around(value = "pointcut(authorityVerify)")
    public Object doAround(ProceedingJoinPoint joinPoint, com.example.demo.admin.annotion.AuthorityVerify.AuthorityVerify authorityVerify) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Request attributes are not available.");
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取请求路径
        String url = request.getRequestURI();

        log.info("url",url);

        // 获取当前用户的角色列表
        List<String> roleList = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 根据角色获取菜单信息
        List<String> menusUrl = this.getMenus(roleList);

        // 检查请求的 URL 是否在用户的权限列表中
        boolean hasAccess = menusUrl.stream()
                .anyMatch(u -> u.equals(url));

        if (hasAccess) {
            // 如果有权限，执行目标方法
            return joinPoint.proceed();
        } else {
            // 没有权限，抛出异常或返回适当的响应
            throw new AccessDeniedException("You do not have permission to access this resource.");
        }
    }
    public List<String> getMenus(List<String> roleNames) {


        List<String> url = new ArrayList<>();
        for (String roleName : roleNames) {
            if(stringRedisTemplate.hasKey(RedisConf.ROLE_MENUS_HASH  + roleName)){
                ArrayList<String> list = JSON.parseObject(
                        stringRedisTemplate.opsForValue().get(RedisConf.ROLE_MENUS_HASH  + roleName),
                        new TypeReference<ArrayList<String>>() {
                        }
                );

                url.addAll(list);
            }else {
                Role role = roleService.getOne(new QueryWrapper<Role>().eq("role_name", roleName));
                if (role != null) {
                    String[] split = role.getCategoryMenuUids().replace("[", "").replace("]", "")
                            .replace("\"", "").split(",");
                    List<String> menuIds = Arrays.asList(split);
                    List<String> list = categoryMenuService.listByIds(menuIds).stream()
                            .filter(item -> item.getMenuLevel() == 3)
                            .map(item -> item.getUrl())
                            .collect(Collectors.toList());
                    stringRedisTemplate.opsForValue().set(RedisConf.ROLE_MENUS_HASH + roleName, JSON.toJSONString(list));
                    url.addAll(list);
                }

            }
        }

        return url;
    }

    public List<String> getUrlsOfLevelThreeMenus(List<CategoryMenu> allMenus) {
        // 假设菜单的层级信息在 parentUid 中
        List<CategoryMenu> levelThreeMenus = allMenus.stream()
                .filter(menu -> menu.getParentUid() != null &&
                        allMenus.stream().anyMatch(parentMenu -> parentMenu.getUid().equals(menu.getParentUid())))
                .filter(menu -> allMenus.stream().anyMatch(parentMenu ->
                        parentMenu.getParentUid() != null &&
                                parentMenu.getParentUid().equals(menu.getParentUid())))
                .collect(Collectors.toList());

        return levelThreeMenus.stream()
                .map(CategoryMenu::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
