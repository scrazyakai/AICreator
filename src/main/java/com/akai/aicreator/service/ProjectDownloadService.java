package com.akai.aicreator.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {
    /**
     * 下载项目
     * @return 下载结果
     */
    public void downloadProjectAsZIP(String projectPath, String downLoadName, HttpServletResponse response);
}
