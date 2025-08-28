package com.akai.aicreator.model.request;

import com.akai.aicreator.model.enums.CodeGenTypeEnum;
import lombok.Data;

/**
 * 创建应用请求
 */
@Data
public class AppCreateRequest {
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    private CodeGenTypeEnum codeGenType;
}