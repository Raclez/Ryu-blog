package com.example.demo.resource.service;

import com.example.demo.commons.entity.File;
import com.example.demo.commons.entity.SystemConfig;
import com.example.demo.base.service.SuperService;
import com.example.demo.base.vo.FileVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件服务类
 *
 * @author
 * @since
 */
public interface FileService extends SuperService<File> {

    /**
     * 截图上传
     *
     * @param multipartFileList
     * @return
     */
    public String cropperPicture(@Param("multipartFileList") List<MultipartFile> multipartFileList);

    /**
     * 通过fileIds获取图片信息
     *
     * @param fileIds
     * @param code
     * @return
     */
    public String getPicture(String fileIds, String code);

    /**
     * 批量文件上传
     *
     * @param request
     * @param multipartFileList
     * @param systemConfig
     * @return
     */
    String batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList, SystemConfig systemConfig);

    /**
     * 通过URL上传图片
     *
     * @param fileVO
     * @return
     */
    String uploadPictureByUrl(FileVO fileVO);

    /**
     * CKeditor图像中的图片上传
     *
     * @param request
     * @return
     */
    Object ckeditorUploadFile(HttpServletRequest request);

    /**
     * CKeditor上传 复制的图片
     *
     * @return
     */
    Object ckeditorUploadCopyFile();

    /**
     * 工具栏 “插入\编辑超链接”的文件上传
     *
     * @return
     */
    Object ckeditorUploadToolFile(HttpServletRequest request);
}
