package com.akai.aicreator.model.request;

import cn.hutool.core.date.DateTime;
import com.akai.aicreator.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对话历史查询请求
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatHistoryQueryRequest extends PageRequest {

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 消息类型（user/ai）
     */
    private String messageType;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 消息内容（支持模糊查询）
     */
    private String message;
    /**
     * 创建时间
     */
    private DateTime lastCreateTime;
    /**
     * 排序字段
     */
    private String sortField;
    /**
     * 排序方式：asc/desc
     */
    private String sortOrder;
}