package com.example.demo.resource.service.impl;

import com.example.demo.commons.entity.FileSort;
import com.example.demo.resource.mapper.FileSortMapper;
import com.example.demo.resource.service.FileSortService;
import com.example.demo.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author  Ryu
 * @since 2021-08-17
 */
@Service
public class FileSortServiceImpl extends SuperServiceImpl<FileSortMapper, FileSort> implements FileSortService {

}
