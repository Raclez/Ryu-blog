package com.example.demo.resource.service;

import com.example.demo.commons.entity.FileSort;
import com.example.demo.commons.entity.SystemConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ResourceService {

    void uploadFile(String fileName, InputStream inputStream) throws Exception;
    InputStream getFile(String fileName) throws Exception;
    void deleteFile(String fileName) throws Exception;
    String getFileUrl(String fileName);
}
