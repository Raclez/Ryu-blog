package com.example.demo.admin.annotion.AvoidRepeatableCommit;

import com.example.demo.utils.IpUtils;
import com.example.demo.utils.RedisUtil;
import com.example.demo.utils.ResultUtil;
import com.example.demo.utils.StringUtils;
import com.example.demo.xo.global.RedisConf;
import com.example.demo.xo.global.SysConf;
import com.example.demo.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 避免接口重复提交AOP
 *
 * @author:
 * @create:
 */
@Aspect
@Component
@Slf4j
public class AvoidRepeatableCommitAspect {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * @param point
     */
    @Around("@annotation(com.example.demo.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        HttpServletRequest request = RequestHolder.getRequest();

        String ip = IpUtils.getIpAddr(request);

        //获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //目标类、方法
        String className = method.getDeclaringClass().getName();

        String name = method.getName();

        // 得到类名和方法
        String ipKey = String.format("%s#%s", className, name);

        // 转换成HashCode
        int hashCode = Math.abs(ipKey.hashCode());

        String key = String.format("%s:%s_%d", RedisConf.AVOID_REPEATABLE_COMMIT, ip, hashCode);

        log.info("ipKey={},hashCode={},key={}", ipKey, hashCode, key);

        com.example.demo.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit avoidRepeatableCommit = method.getAnnotation(com.example.demo.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit.class);

        long timeout = avoidRepeatableCommit.timeout();

        String value = redisUtil.get(key);

        if (StringUtils.isNotBlank(value)) {
            log.info("请勿重复提交表单");
            return ResultUtil.result(SysConf.ERROR, "请勿重复提交表单");
        }

        // 设置过期时间
        redisUtil.setEx(key, StringUtils.getUUID(), timeout, TimeUnit.MILLISECONDS);

        //执行方法
        Object object = point.proceed();
        return object;
    }

}
