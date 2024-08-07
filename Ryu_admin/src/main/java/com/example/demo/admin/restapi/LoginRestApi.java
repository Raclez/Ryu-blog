package com.example.demo.admin.restapi;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.admin.global.MessageConf;
import com.example.demo.admin.global.RedisConf;
import com.example.demo.admin.global.SQLConf;
import com.example.demo.admin.global.SysConf;
import com.example.demo.admin.security.SecurityUserDetailsServiceImpl;
import com.example.demo.commons.config.jwt.Audience;
import com.example.demo.commons.config.jwt.JwtTokenUtil;
import com.example.demo.commons.config.security.SecurityUser;
import com.example.demo.commons.entity.Admin;
import com.example.demo.commons.entity.CategoryMenu;
import com.example.demo.commons.entity.OnlineAdmin;
import com.example.demo.commons.entity.Role;
import com.example.demo.commons.feign.PictureFeignClient;
import com.example.demo.utils.*;
import com.example.demo.xo.service.AdminService;
import com.example.demo.xo.service.CategoryMenuService;
import com.example.demo.xo.service.RoleService;
import com.example.demo.xo.service.WebConfigService;
import com.example.demo.xo.utils.WebUtil;
import com.example.demo.base.enums.EMenuType;
import com.example.demo.base.enums.EStatus;
import com.example.demo.base.global.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录管理 RestApi【为了更好地使用security放行把登录管理放在AuthRestApi中】
 *
 * 
 * 
 */
@RestController
@RefreshScope
@RequestMapping("/auth")
@Api(value = "登录相关接口", tags = {"登录相关接口"})
@Slf4j
public class LoginRestApi {

    @Autowired
    private WebUtil webUtil;
    @Autowired
        private AdminService adminService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CategoryMenuService categoryMenuService;
    @Autowired
    private Audience audience;
    @Value("${singleLogin}")
    private boolean singleLogin;
    @Value(value = "${tokenHead}")
    private String tokenHead;
    @Value(value = "${isRememberMeExpiresSecond}")
    private int isRememberMeExpiresSecond;
    /**
     * token过期的时间
     */
    @Value(value = "${audience.expiresSecond}")
    private Long expiresSecond;


    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    public String login(HttpServletRequest request,
                        @ApiParam(name = "username", value = "用户名或邮箱或手机号") @RequestParam(name = "username", required = false) String username,
                        @ApiParam(name = "password", value = "密码") @RequestParam(name = "password", required = false) String password,
                        @ApiParam(name = "isRememberMe", value = "是否记住账号密码") @RequestParam(name = "isRememberMe", required = false, defaultValue = "false") Boolean isRememberMe) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResultUtil.result(SysConf.ERROR, "账号或密码不能为空");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityUser admin = (SecurityUser) authentication.getPrincipal();
        // 对密码进行加盐加密验证，采用SHA-256 + 随机盐【动态加盐】 + 密钥对密码进行加密
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isPassword = encoder.matches(password, admin.getPassword());
        if (!isPassword) {
            //密码错误，返回提示
            log.error("管理员密码错误");
            return ResultUtil.result(SysConf.ERROR, String.format(MessageConf.LOGIN_ERROR, setLoginCommit(request)));
        }
//        if(singleLogin){
//                adminService.checkSameUser(username);
//        }
//

        long expiration = isRememberMe ? isRememberMeExpiresSecond : audience.getExpiresSecond();
        String jwtToken = jwtTokenUtil.createJWT(admin.getUsername(),
                admin.admin.getUid(),
                admin.getAuthorities().toString(),
                audience.getClientId(),
                audience.getName(),
                expiration * 1000,
                audience.getBase64Secret());
        String token = tokenHead + jwtToken;
        Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
        result.put(SysConf.TOKEN, token);
        String oldToken = stringRedisTemplate.opsForValue().get("auth:username:" + admin.admin.getUserName());

        if (StringUtils.isNotEmpty(oldToken)) {
            stringRedisTemplate.delete("auth:token:" + oldToken);
        }
        stringRedisTemplate.opsForValue().set("auth:token:" + token, admin.getUsername(), expiresSecond, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("auth:username:"+admin.getUsername(),token,expiresSecond,TimeUnit.SECONDS);
        Admin loginAdmin = admin.admin;

        //进行登录相关操作
        Integer count = loginAdmin.getLoginCount() + 1;
        loginAdmin.setLoginCount(count);
        loginAdmin.setLastLoginIp(IpUtils.getIpAddr(request));
        loginAdmin.setLastLoginTime(new Date());
//        // 设置token到validCode，用于记录登录用户
        loginAdmin.setValidCode(token);
        adminService.updateById(loginAdmin);
//        // 设置tokenUid，【主要用于换取token令牌，防止token直接暴露到在线用户管理中】
//        admin.setTokenUid(StringUtils.getUUID());
//        admin.setRole(roles.get(0));
//        // 添加在线用户到Redis中【设置过期时间】
//        adminService.addOnlineAdmin(admin, expiration);
        return ResultUtil.result(SysConf.SUCCESS, result);
    }

    @ApiOperation(value = "用户信息", notes = "用户信息", response = String.class)
    @GetMapping(value = "/info")
    public String info(HttpServletRequest request,
                       @ApiParam(name = "token", value = "token令牌", required = false) @RequestParam(name = "token", required = false) String token) {

        Map<String, Object> map = new HashMap<>(Constants.NUM_THREE);
//        if (request.getAttribute(SysConf.ADMIN_UID) == null) {
//            return ResultUtil.result(SysConf.ERROR, "token用户过期");
//        }
        SecurityUser principal = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Admin admin = adminService.getById(principal.admin.getUid());
        map.put(SysConf.TOKEN, token);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(admin.getAvatar(), SysConf.FILE_SEGMENTATION);
            List<String> list = webUtil.getPicture(pictureList);
            if (list.size() > 0) {
                map.put(SysConf.AVATAR, list.get(0));
            } else {
                map.put(SysConf.AVATAR, "https://educa-10.oss-cn-beijing.aliyuncs.com/2021-08-16/e19c0629728a475786d99301f6ee017bfavicon48.ico");
            }
        }

        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        Collection<Role> roleList = roleService.listByIds(roleUid);
        map.put(SysConf.ROLES, roleList);
        return ResultUtil.result(SysConf.SUCCESS, map);
    }

    @ApiOperation(value = "获取当前用户的菜单", notes = "获取当前用户的菜单", response = String.class)
    @GetMapping(value = "/getMenu")

    public String getMenu(HttpServletRequest request) {

        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Collection<? extends GrantedAuthority> authorities = securityUser.getAuthorities();
        List<String> roleName = authorities.stream().map(authorit -> authorit.getAuthority()).collect(Collectors.toList());

        Map<String, Object> map = categoryMenuService.getMenusByUser(roleName);

        return ResultUtil.result(SysConf.SUCCESS, map);
    }



    @ApiOperation(value = "获取网站名称", notes = "获取网站名称", response = String.class)
    @GetMapping(value = "/getWebSiteName")
    public String getWebSiteName() {
        return ResultUtil.successWithData(webConfigService.getWebSiteName());
    }


    @ApiOperation(value = "退出登录", notes = "退出登录", response = String.class)
    @PostMapping(value = "/logout")
    public String logout() {
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();
        String token = request.getAttribute(SysConf.TOKEN).toString();
        if (StringUtils.isEmpty(token)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        } else {
            // 获取在线用户信息
            String adminJson = redisUtil.get(RedisConf.LOGIN_TOKEN_KEY + RedisConf.SEGMENTATION + token);
            if (StringUtils.isNotEmpty(adminJson)) {
                OnlineAdmin onlineAdmin = JsonUtils.jsonToPojo(adminJson, OnlineAdmin.class);
                String tokenUid = onlineAdmin.getTokenId();
                // 移除Redis中的TokenUid
                redisUtil.delete(RedisConf.LOGIN_UUID_KEY + RedisConf.SEGMENTATION + tokenUid);
            }
            // 移除Redis中的用户
            redisUtil.delete(RedisConf.LOGIN_TOKEN_KEY + RedisConf.SEGMENTATION + token);
            SecurityContextHolder.clearContext();
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
        }
    }

    /**
     * 设置登录限制，返回剩余次数
     * 密码错误五次，将会锁定30分钟
     *
     * @param request
     */
    private Integer setLoginCommit(HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        String count = redisUtil.get(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip);
        Integer surplusCount = 5;
        if (StringUtils.isNotEmpty(count)) {
            Integer countTemp = Integer.valueOf(count) + 1;
            surplusCount = surplusCount - countTemp;
            redisUtil.setEx(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip, String.valueOf(countTemp), 30, TimeUnit.MINUTES);
        } else {
            surplusCount = surplusCount - 1;
            redisUtil.setEx(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip, Constants.STR_ONE, 30, TimeUnit.MINUTES);
        }
        return surplusCount;
    }

}
