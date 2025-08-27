package com.akai.aicreator.model.request;

import com.akai.aicreator.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryRequest extends PageRequest {
    /**
     * 应用名称（支持模糊查询）
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 创建用户id
     */
    private Long userId;
}