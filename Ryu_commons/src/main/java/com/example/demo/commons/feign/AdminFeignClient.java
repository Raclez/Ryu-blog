package com.example.demo.commons.feign;

import com.example.demo.commons.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 后台服务feign远程调用
 *
 * @author 
 * @date 2021年1月21日22:19:10
 */

@FeignClient(name = "Ryu-admin", configuration = FeignConfiguration.class)
public interface AdminFeignClient {


    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/systemConfig/getSystemConfig", method = RequestMethod.GET)
    public String getSystemConfig();


}