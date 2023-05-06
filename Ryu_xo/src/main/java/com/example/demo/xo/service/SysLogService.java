package com.example.demo.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.commons.entity.SysLog;
import com.example.demo.xo.vo.SysLogVO;
import com.example.demo.base.service.SuperService;

/**
 * 操作日志 服务类
 *
 * 
 * 
 */
public interface SysLogService extends SuperService<SysLog> {

    /**
     * 获取操作日志列表
     *
     * @param sysLogVO
     * @return
     */
    public IPage<SysLog> getPageList(SysLogVO sysLogVO);
}
