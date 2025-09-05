package com.akai.aicreator.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ZipUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.service.IUserService;
import com.akai.aicreator.service.ProjectDownloadService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {
    @Override
    public void downloadProjectAsZIP(String projectPath, String downLoadName, HttpServletResponse response) {
        if(projectPath == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"项目路径不能为空");
        }
        if(downLoadName == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"下载名称不能为空");
        }
        File projectDir = new File(projectPath);
        if(!projectDir.exists()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"项目路径不存在");
        }
        if(!projectDir.isDirectory()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"项目路径不是目录");
        }
        log.info("开始下载项目: {}->{}.zip", projectPath,downLoadName);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=" + downLoadName + ".zip");
        FileFilter fileFilter = file->isPathAllowed(projectDir.toPath(),file.toPath());
        try {
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8,false,fileFilter,projectDir);
            log.info("项目下载完成: {}->{}.zip", projectPath,downLoadName);
        } catch (IOException e) {
            log.info("项目下载失败: {}->{}.zip", projectPath,downLoadName,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"项目下载失败");
        }

    }

    /**
     * 需要过滤的文件和目录名称
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    /**
     * 需要过滤的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );

    /**
     * 检查路径是否允许包含在压缩包中
     *
     * @param projectRoot 项目根目录
     * @param fullPath    完整路径
     * @return 是否允许
     */
    private boolean isPathAllowed(Path projectRoot, Path fullPath) {
        // 获取相对路径
        Path relativePath = projectRoot.relativize(fullPath);
        // 检查路径中的每一部分
        for (Path part : relativePath) {
            String partName = part.toString();
            // 检查是否在忽略名称列表中
            if (IGNORED_NAMES.contains(partName)) {
                return false;
            }
            // 检查文件扩展名
            if (IGNORED_EXTENSIONS.stream().anyMatch(partName::endsWith)) {
                return false;
            }
        }
        return true;
    }


}
