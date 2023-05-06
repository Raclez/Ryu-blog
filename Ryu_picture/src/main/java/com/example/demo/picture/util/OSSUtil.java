package com.example.demo.picture.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.example.demo.picture.config.PutObjectProgressListener;
import com.example.demo.picture.global.SysConf;
import com.example.demo.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.aliyun.oss.internal.OSSConstants.URL_ENCODING;

/**
 * @author RL475
 * <p>
 * OSS工具类
 */
@Slf4j
@Component
@Controller
public class OSSUtil {
    @Autowired
    FeignUtil feignUtil;

    String bucket = "educa-10";
    String endpoint = "oss-cn-beijing.aliyuncs.com";
    String accessId = "LTAI5t6gjLKPb1rDfpq1DwF7";
    String accessKeySecret = "XA0SgX5tLMRWCFY5ZO5EtTuFkNfSEd";

    // 创建OSSClient实例。
    OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKeySecret);

    /**
     * @param ossconfig oss的配置
     * @param list      多个文件
     *                  多个文件删除
     */
    public static List<String> deleteBuchFile(Map<String, String> ossconfig, List<String> list) {
        String ossAccessKey = ossconfig.get(SysConf.OSS_ACCESS_KEY);
        String ossSecretKey = ossconfig.get(SysConf.OSS_SECRET_KEY);
        String ossEnpoint = ossconfig.get(SysConf.OSS_ENPOINT);
        String ossBucket = ossconfig.get(SysConf.OSS_BUCKET);
        OSS ossClient = new OSSClientBuilder().build(ossEnpoint, ossAccessKey, ossSecretKey);

        // 删除文件。
        // 填写需要删除的多个文件完整路径。文件完整路径中不能包含Bucket名称。

        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(ossBucket).withKeys(list).withEncodingType(URL_ENCODING));
        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
        try {
            for (String obj : deletedObjects) {
                String deleteObj = URLDecoder.decode(obj, "UTF-8");
                System.out.println(deleteObj);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 关闭OSSClient。
        ossClient.shutdown();
        return deletedObjects;
    }

    public static void deleteBuchfile(Map<String, String> qiNiuConfig, List<String> fileList) {
    }

    /**
     * oss文件上传回调
     *
     * @return
     */

    /* public Map<String, String> uploadfile(@RequestParam("file") MultipartFile file*//*,SystemConfig ossConfig*//*) {


        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKeySecret);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("C:\\Users\\RL475\\Desktop\\背景\\1.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, "1", inputStream);

        String host = "https://" + bucket + "." + endpoint;
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String callbackUrl = "http://localhost:8602";
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format; // 用户上传文件时指定的前缀。
        Map<String, String> respMap = null;
        // 创建OSSClient实例。

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));
            ossClient.putObject(putObjectRequest);

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            log.info(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
          return respMap;
    }*/
    public String ossUploadFile(MultipartFile file, OSS ossClient) {
// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = null;
        String fileName = null;
        String format = null;
        try {
            inputStream = file.getInputStream();

            //获取文件名称
            fileName = file.getOriginalFilename();
            format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
// 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucket, format + "/" + fileName, inputStream).<PutObjectRequest>withProgressListener(new PutObjectProgressListener()));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }

        return format + "/" + fileName;
    }

    /**
     * oss查询文件信息
     * <p>
     * Last-Modified	Object的最后修改时间。
     * Content-Length	Object的大小，单位为字节
     */
    @Test
    public void ossfiledata() throws IOException {
//        String bucket = "educa-10";
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        String accessId = "LTAI5t6gjLKPb1rDfpq1DwF7";
//        String accessKeySecret = "XA0SgX5tLMRWCFY5ZO5EtTuFkNfSEd";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKeySecret);
//        File file = new File("C:\\Users\\RL475\\Desktop\\Java截图\\屏幕截图 2020-10-20 224206.png");
//        FileInputStream input = new FileInputStream(file);
//        MockMultipartFile mockMultipartFile = new MockMultipartFile("aa", "aa", "image/png", input);
//        String s = ossUploadfile(mockMultipartFile, ossClient);
//        System.out.println(s);
        System.out.println(MD5Utils.string2MD5("123456"));

    }

    /**
     * oss删除文件(目录下的所有文件)
     * <p>
     * pathName 删除的文件名
     */

    public void ossDeletefile(Map<String, String> ossconfig, String prefix) {

        String ossAccessKey = ossconfig.get(SysConf.OSS_ACCESS_KEY);
        String ossSecretKey = ossconfig.get(SysConf.OSS_SECRET_KEY);
        String ossEnpoint = ossconfig.get(SysConf.OSS_ENPOINT);
        String ossBucket = ossconfig.get(SysConf.OSS_BUCKET);
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ossEnpoint, ossAccessKey, ossSecretKey);

        // 填写不包含Bucket名称在内的目录完整路径。例如Bucket下testdir目录的完整路径为testdir/。
        // 删除目录及目录下的所有文件。
        String nextMarker = null;
        ObjectListing objectListing = null;
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket)
                    .withPrefix(prefix)
                    .withMarker(nextMarker);

            objectListing = ossClient.listObjects(listObjectsRequest);
            if (objectListing.getObjectSummaries().size() > 0) {
                List<String> keys = new ArrayList<String>();
                for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                    System.out.println("key name: " + s.getKey());
                    keys.add(s.getKey());
                }
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keys);
                ossClient.deleteObjects(deleteObjectsRequest);
            }

            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());

// 关闭OSSClient。
        ossClient.shutdown();
    }

    public void deletesimpaglefile(OSS ossClient, String fileName, String bucketName) {
        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, fileName);
        // 关闭OSSClient。
        ossClient.shutdown();
    }


    /**
     * 得到Oss配置
     * @return
     */
//    public Map<String, String> getOssConfig() {
//        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attribute.getRequest();
//        // 后台携带的token
//        Object token = request.getAttribute(SysConf.TOKEN);
//        // 参数中携带的token
//        String paramsToken = request.getParameter(SysConf.TOKEN);
//        // 获取平台【web：门户，admin：管理端】
//        String platform = request.getParameter(SysConf.PLATFORM);
//        Map<String, String> ossResultMap = new HashMap<>();
//        // 判断是否是web端发送过来的请求【后端发送过来的token长度为32】
//        if (SysConf.WEB.equals(platform) || paramsToken.length() == Constants.THIRTY_TWO) {
//            // 如果是调用web端获取配置的接口
//            ossResultMap = feignUtil.getSystemConfigByWebToken(paramsToken);
//        } else {
//            // 调用admin端获取配置接口
//            if (token != null) {
//                // 判断是否是后台过来的请求
//                ossResultMap = feignUtil.getSystemConfigMap(token.toString());
//            } else {
//                // 判断是否是通过params参数传递过来的
//                ossResultMap = feignUtil.getSystemConfigMap(paramsToken);
//            }
//        }
//
//        if (ossResultMap == null) {
//            log.error(MessageConf.PLEASE_SET_OSS);
//            throw new QueryException(ErrorCode.PLEASE_SET_OSS, MessageConf.PLEASE_SET_OSS);
//        }
//
//        String uploadoss = ossResultMap.get(SysConf.UPLOAD_OSS);
//        String uploadLocal = ossResultMap.get(SysConf.UPLOAD_LOCAL);
//        String localPictureBaseUrl = ossResultMap.get(SysConf.LOCAL_PICTURE_BASE_URL);
//        String ossPictureBaseUrl = ossResultMap.get(SysConf.OSS_PICTURE_BASE_URL);
//        String ossAccessKey = ossResultMap.get(SysConf.OSS_ACCESS_KEY);
//        String ossSecretKey = ossResultMap.get(SysConf.OSS_SECRET_KEY);
//        String ossBucket = ossResultMap.get(SysConf.OSS_BUCKET);
//        String ossenpoint = ossResultMap.get(SysConf.OSS_ENPOINT);
//        String picturePriority = ossResultMap.get(SysConf.PICTURE_PRIORITY);
//
//        if (EOpenStatus.OPEN.equals(uploadoss) && (StringUtils.isEmpty(ossPictureBaseUrl) || StringUtils.isEmpty(ossAccessKey)
//                || StringUtils.isEmpty(ossSecretKey) || StringUtils.isEmpty(ossBucket) )) {
//            log.error(MessageConf.PLEASE_SET_OSS);
//            throw new QueryException(ErrorCode.PLEASE_SET_OSS, MessageConf.PLEASE_SET_OSS);
//        }
//
//        if (EOpenStatus.OPEN.equals(uploadLocal) && StringUtils.isEmpty(localPictureBaseUrl)) {
//            log.error(MessageConf.PLEASE_SET_QI_NIU);
//            throw new QueryException(ErrorCode.PLEASE_SET_LOCAL, MessageConf.PLEASE_SET_LOCAL);
//        }
//
//        // oss配置
//        Map<String, String> ossConfig = new HashMap<>();
//        ossConfig.put(SysConf.OSS_ACCESS_KEY, ossAccessKey);
//        ossConfig.put(SysConf.OSS_SECRET_KEY, ossSecretKey);
//        ossConfig.put(SysConf.OSS_BUCKET, ossBucket);
//        ossConfig.put(SysConf.OSS_ENPOINT, ossenpoint);
//        ossConfig.put(SysConf.UPLOAD_OSS, uploadoss);
//        ossConfig.put(SysConf.UPLOAD_LOCAL, uploadLocal);
//        ossConfig.put(SysConf.PICTURE_PRIORITY, picturePriority);
//        ossConfig.put(SysConf.LOCAL_PICTURE_BASE_URL, localPictureBaseUrl);
//        ossConfig.put(SysConf.OSS_PICTURE_BASE_URL, ossPictureBaseUrl);
//        return ossConfig;
//    }

}



