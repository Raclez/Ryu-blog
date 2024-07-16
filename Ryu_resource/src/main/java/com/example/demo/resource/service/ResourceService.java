package com.example.demo.resource.service;

import com.example.demo.commons.entity.FileSort;
import com.example.demo.commons.entity.SystemConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResourceService {

    /**
     * 多文件上传
     *
     * @param multipartFileList
     * @return
     * @throws IOException
     */
    List<String> batchUploadFile(List<MultipartFile> multipartFileList, SystemConfig systemConfig);

    /**
     * 文件上传
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    String uploadFile(MultipartFile multipartFile,SystemConfig systemConfig);

    /**
     * 通过URL上传图片
     *
     * @param url
     * @return
     */
    String uploadPictureByUrl(String url,SystemConfig systemConfig);
}
