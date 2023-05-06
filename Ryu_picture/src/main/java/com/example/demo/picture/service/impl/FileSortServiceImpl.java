package com.example.demo.picture.service.impl;

import com.example.demo.commons.entity.FileSort;
import com.example.demo.picture.mapper.FileSortMapper;
import com.example.demo.picture.service.FileSortService;
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
