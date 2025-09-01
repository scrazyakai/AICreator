package com.akai.aicreator.manner;

import com.akai.aicreator.config.OssConfig;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Date;

/**
 * OSS 对象存储管理器
 *
 * @author 
 */
@Component
@Slf4j
public class OssManager {

    @Resource
    private OSS ossClient;

    @Resource
    private OssConfig ossConfig;

    /**
     * 上传文件到 OSS
     *
     * @param key  存储对象的唯一键 (例如 "uploads/test.png")
     * @param file 本地文件
     */
    public PutObjectResult putObject(String key,File file) {
        return ossClient.putObject(new PutObjectRequest(
                ossConfig.getBucketName(),
                key,
                file
        ));
    }
    public String uploadFile(String key,File file) {
        PutObjectResult putObjectResult = putObject(key, file);
        if(putObjectResult != null) {
            //构建URL
            String url = String.format("https://%s.%s/%s",
                    ossConfig.getBucketName(),
                    ossConfig.getEndpoint(),
                    key);
            log.info("文件上传成功:{}",url);
            return url;
        }else{
            log.error("上传文件失败");
            return null;
        }

    }

}
