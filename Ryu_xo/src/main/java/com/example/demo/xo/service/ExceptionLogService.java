package com.example.demo.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.commons.entity.ExceptionLog;
import com.example.demo.xo.vo.ExceptionLogVO;
import com.example.demo.base.service.SuperService;

/**
 * 操作异常日志 服务类
 *
 * 
 * 
 */
public interface ExceptionLogService extends SuperService<ExceptionLog> {

    /**
     * 获取异常日志列表
     *
     * @param exceptionLogVO
     * @return
     */
    public IPage<ExceptionLog> getPageList(ExceptionLogVO exceptionLogVO);
}
