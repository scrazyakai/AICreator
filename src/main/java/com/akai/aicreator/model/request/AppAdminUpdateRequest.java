package com.akai.aicreator.model.request;

import lombok.Data;

/**
 * 管理员更新应用请求
 */
@Data
public class AppAdminUpdateRequest {
    /**
     * 应用ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 优先级
     */
    private Integer priority;
}