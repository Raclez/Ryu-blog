package com.example.demo.resource.service;

import com.example.demo.commons.entity.SystemConfig;
import org.springframework.web.multipart.MultipartFile;

/**
 * Oss服务类
 * @author RL475
 *
 *
 */
public interface OssService {
    /**
     * 文件上传
     * @param   file  文件
     * @return   String   返回成功是否
     */
    String uploadFile(MultipartFile file);

    /**
     *
     *多个文件的上传
     * @param files  多个文件
     * @return  String
     */
    String batchUploadFiles(MultipartFile []files);
    void deleteFile(String fileName);

    /**
     * 通过URL上传图片
     *
     * @param itemUrl   上传地址
     * @param systemConfig
     * @return
     */
    String uploadPictureByUrl(String itemUrl, SystemConfig systemConfig);
}
