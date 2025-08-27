package com.akai.aicreator.model.request;

import lombok.Data;

/**
 * 更新应用请求
 */
@Data
public class AppUpdateRequest {
    /**
     * 应用ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;
}