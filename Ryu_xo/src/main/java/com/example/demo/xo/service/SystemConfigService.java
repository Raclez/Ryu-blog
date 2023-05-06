package com.example.demo.xo.service;

import com.example.demo.commons.entity.SystemConfig;
import com.example.demo.xo.vo.SystemConfigVO;
import com.example.demo.base.service.SuperService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
/**
 * 系统配置表 服务类
 *
 * @author  Ryu
 * @datge 2021年1月21日09:05:53
 */
public interface SystemConfigService extends SuperService<SystemConfig> {

    /**
     * 获取系统配置
     *
     * @return
     */
    public SystemConfig getConfig();

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param key
     * @return
     */
    public String cleanRedisByKey(List<String> key);

    /**
     * 修改系统配置
     *
     * @param systemConfigVO
     * @return
     */
    public String editSystemConfig(SystemConfigVO systemConfigVO);
}
