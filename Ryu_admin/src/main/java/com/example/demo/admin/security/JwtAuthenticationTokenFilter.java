package com.example.demo.admin.security;

import com.alibaba.fastjson.JSON;
import com.example.demo.admin.global.RedisConf;
import com.example.demo.admin.global.SysConf;

import com.example.demo.base.global.Constants;
import com.example.demo.commons.config.jwt.Audience;
import com.example.demo.commons.config.jwt.JwtTokenUtil;
import com.example.demo.commons.config.security.SecurityUser;
import com.example.demo.commons.entity.OnlineAdmin;
import com.example.demo.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器 【验证token有效性】
 *
 * @author
 * @date
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
    private Audience audience;

    @Resource
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value(value = "${tokenHead}")
    private String tokenHead;

    @Value(value = "${tokenHeader}")
    private String tokenHeader;

    /**
     * token过期的时间
     */
    @Value(value = "${audience.expiresSecond}")
    private Long expiresSecond;

    /**
     * token刷新的时间
     */
    @Value(value = "${audience.refreshSecond}")
    private Long refreshSecond;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        //得到请求头信息authorization信息
        String authHeader = request.getHeader(tokenHeader);

        //TODO 判断是否触发 ryu-resource发送的请求【图片上传鉴权，需要用户登录，携带token请求admin，后期考虑加入OAuth服务统一鉴权】
        final String pictureToken = request.getHeader("pictureToken");
        if (StringUtils.isNotEmpty(pictureToken)) {
            authHeader = pictureToken;
        }

        //请求头 'Authorization': tokenHead + token
        if (!StringUtils.isEmpty(authHeader) && authHeader.startsWith(tokenHead)) {

            log.error("传递过来的token为: {}", authHeader);
            // 私钥
            String base64Secret = audience.getBase64Secret();

            String name = stringRedisTemplate.opsForValue().get("auth:token:"+authHeader);

            if (name != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 校验Token的有效性
                SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(name);
                final String token = authHeader.substring(tokenHead.length());


                if (jwtTokenUtil.validateToken(token, userDetails, base64Secret)) {


                    //把adminUid存储到request中
            request.setAttribute(SysConf.ADMIN_UID, userDetails.admin.getUid());
                    request.setAttribute(SysConf.USER_NAME, name);
                    request.setAttribute(SysConf.TOKEN, authHeader);
                    log.info("解析出来用户: {}", name);
            log.info("解析出来的用户Uid: {}", userDetails.admin.getUid());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    //以后可以security中取得SecurityUser信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);

    }
}


