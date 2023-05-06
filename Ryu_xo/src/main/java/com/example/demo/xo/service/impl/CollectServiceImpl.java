package com.example.demo.xo.service.impl;

import com.example.demo.commons.entity.Collect;
import com.example.demo.xo.mapper.CollectMapper;
import com.example.demo.xo.service.CollectService;
import com.example.demo.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 收藏表 服务实现类
 *
 * @author  Ryu
 * @since 2018-09-08
 */
@Service
public class CollectServiceImpl extends SuperServiceImpl<CollectMapper, Collect> implements CollectService {

}
