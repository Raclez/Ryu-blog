package com.example.demo.admin.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.admin.global.MessageConf;
import com.example.demo.admin.global.SQLConf;
import com.example.demo.admin.global.SysConf;
import com.example.demo.base.enums.EStatus;
import com.example.demo.commons.config.security.SecurityUser;
import com.example.demo.commons.entity.Admin;
import com.example.demo.commons.entity.Role;
import com.example.demo.utils.CheckUtils;
import com.example.demo.xo.service.AdminService;
import com.example.demo.xo.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 将SpringSecurity中的用户管理和数据库的管理员对应起来
 *
 * @author
 * @date
 */
@Slf4j
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final int MAX_FAILED_ATTEMPTS = 5;
    private final long LOCK_TIME_DURATION = 24; // 锁定时间，单位为小时

    /**
     * @param username 浏览器输入的用户名【需要保证用户名的唯一性】
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("User not found");
        }
//        if (isAccountLocked(username)) {
//            throw new RuntimeException("Account is locked due to too many failed login attempts");
//        }
        Boolean isEmail = CheckUtils.checkEmail(username);
        Boolean isMobile = CheckUtils.checkMobileNumber(username);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (isEmail) {
            queryWrapper.eq(SQLConf.EMAIL, username);
        } else if (isMobile) {
            queryWrapper.eq(SQLConf.MOBILE, username);
        } else {
            queryWrapper.eq(SQLConf.USER_NAME, username);
        }
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        Admin admin = adminService.getOne(queryWrapper);
        if (admin == null) {
            log.error("该管理员不存在");
            throw new UsernameNotFoundException(String.format(MessageConf.LOGIN_ERROR, username));
        } else {
            //查询出角色信息封装到admin中
            List<String> roleNames = new ArrayList<>();
            Role role = roleService.getById(admin.getRoleUid());
            roleNames.add(role.getRoleName());
            admin.setRoleNames(roleNames);
            return SecurityUserFactory.create(admin);
        }
    }

    public void increaseFailedLoginAttempts(String username) {
        String key = "failedAttempts:" + username;
        Integer attempts = Integer.valueOf(redisTemplate.opsForValue().get(key));
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            Admin user = adminService.getAdminByUser(username);
            if (user != null) {
                user.setLocked(true);
                adminService.save(user);
            }
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set("locked:" + username, String.valueOf(true), LOCK_TIME_DURATION, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().set(key, String.valueOf(attempts));
        }
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
    }
    public void resetFailedLoginAttempts(String username) {
        redisTemplate.delete("failedAttempts:" + username);
    }

//    private boolean isAccountLocked(String username) {
//        Boolean isLocked = (Boolean) redisTemplate.opsForValue().get("locked:" + username);
//        return isLocked != null && isLocked;
//    }
}
