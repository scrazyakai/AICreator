package com.akai.aicreator.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.manner.OssManager;
import com.akai.aicreator.service.ScreenshotService;
import com.akai.aicreator.utils.WebScreenshotUtils;
import io.netty.util.internal.ThrowableUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class ScreenshotServiceImpl implements ScreenshotService {
    @Resource
    private OssManager ossManager;
    
    @Override
    public String generateScreenshotAndUpload(String webUrl) {
        if(StrUtil.isBlank(webUrl)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"网站URL不能为空");
        }
        log.info("开始生成截图，URL:{}",webUrl);
        String localScreenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        if(StrUtil.isBlank(localScreenshotPath)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"截图生成失败");
        }
        //上传到OSS
        try {
            String ossScreenshotUrl = uploadScreenshotToOss(localScreenshotPath);
            if(StrUtil.isBlank(ossScreenshotUrl)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"截图上传失败");
            }
            return ossScreenshotUrl;
        }finally {
            clearUpLocalFile(localScreenshotPath);
        }
    }

    private void clearUpLocalFile(String localScreenshotPath) {
        File file = new File(localScreenshotPath);
        if(file.exists()){
            File parentFile = file.getParentFile();
            FileUtil.del(parentFile);
            log.info("清理本地文件:{}",localScreenshotPath);
        }


    }

    private String uploadScreenshotToOss(String localScreenshotPath) {
        if(localScreenshotPath == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"本地截图路径不能为空");
        }
        File file = new File(localScreenshotPath);
        if(!file.exists()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "本地截图文件不存在");
        }
        String fileName = UUID.randomUUID().toString().substring(0,8) + "_compressed.jpg";
        String OssKey = generateOssKey(fileName);
        return ossManager.uploadFile(OssKey,file);

    }

    private String generateOssKey(String fileName) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("screenshots/%s/%s",datePath,fileName);
    }

}
