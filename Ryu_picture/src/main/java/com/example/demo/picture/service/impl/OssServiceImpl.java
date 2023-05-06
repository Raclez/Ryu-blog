package com.example.demo.picture.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.example.demo.base.enums.EOpenStatus;
import com.example.demo.base.exception.exceptionType.InsertException;
import com.example.demo.base.exception.exceptionType.QueryException;
import com.example.demo.base.global.Constants;
import com.example.demo.base.global.ErrorCode;
import com.example.demo.commons.entity.SystemConfig;
import com.example.demo.picture.global.MessageConf;
import com.example.demo.picture.global.SysConf;
import com.example.demo.picture.service.OssService;
import com.example.demo.picture.util.FeignUtil;
import com.example.demo.picture.util.OSSUtil;
import com.example.demo.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RefreshScope
public class OssServiceImpl implements OssService {
    @Autowired
    OSSUtil ossUtil;
    @Autowired
    FeignUtil feignUtil;

    @Override
    public String uploadFile(MultipartFile file) {

        SystemConfig systemConfig = feignUtil.getSystemConfig();
        String accesskey = systemConfig.getOssAccessKey();
        String secretkey = systemConfig.getOssSecretKey();
        String bucket = systemConfig.getOssBucket();
        String enpoint = systemConfig.getOssEndpoin();
        OSS ossClient = new OSSClientBuilder().build(enpoint, accesskey, secretkey);

        String filepath = ossUtil.ossUploadFile(file, ossClient);

        return filepath;

    }

    @Override
    public String batchUploadFiles(MultipartFile[] files) {
        return null;
    }


    @Override
    public void deleteFile(String fileName) {
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        String accesskey = systemConfig.getOssAccessKey();
        String secretkey = systemConfig.getOssSecretKey();
        String bucket = systemConfig.getOssBucket();
        String enpoint = systemConfig.getOssEndpoin();
        OSS ossClient = new OSSClientBuilder().build(enpoint, accesskey, secretkey);
        ossUtil.deletesimpaglefile(ossClient, fileName, bucket);

    }
    @Override
    public String uploadPictureByUrl(String itemUrl, SystemConfig systemConfig) {
        String accesskey = systemConfig.getOssAccessKey();
        String secretkey = systemConfig.getOssSecretKey();
        String bucket = systemConfig.getOssBucket();
        String enpoint = systemConfig.getOssEndpoin();
        OSS ossClient = new OSSClientBuilder().build(enpoint, accesskey, secretkey);

        java.io.File dest = null;
        // 将图片上传到本地服务器中以及oss中
        BufferedOutputStream out = null;
        FileOutputStream os = null;
        // 输入流
        InputStream inputStream = null;
        //获取新文件名 【默认为jpg】
        String newFileName = System.currentTimeMillis() + ".jpg";
        try {
            // 构造URL
            URL url = new URL(itemUrl);
            // 打开连接
            URLConnection con = url.openConnection();
            // 设置用户代理
            con.setRequestProperty("User-agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
            // 设置10秒
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            // 当获取的相片无法正常显示的时候，需要给一个默认图片
            inputStream = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            //暂时使用地址
            String tempFiles = "C:\\Users\\RL475\\Desktoptemp\\" + newFileName;
            dest = new java.io.File(tempFiles);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            os = new FileOutputStream(dest, true);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            FileInputStream fileInputStream = new FileInputStream(dest);
            MultipartFile fileData = new MockMultipartFile(dest.getName(), dest.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            out = new BufferedOutputStream(new FileOutputStream(dest));
            OSSUtil qn = new OSSUtil();
            out.write(fileData.getBytes());

            // TODO 不关闭流，小图片就无法显示？
            out.flush();
            out.close();
            return qn.ossUploadFile(fileData, ossClient);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InsertException(ErrorCode.SYSTEM_CONFIG_NOT_EXIST, MessageConf.SYSTEM_CONFIG_NOT_EXIST);
        } finally {
            if (dest != null && dest.getParentFile().exists()) {
                dest.delete();
            }
        }
    }
}
