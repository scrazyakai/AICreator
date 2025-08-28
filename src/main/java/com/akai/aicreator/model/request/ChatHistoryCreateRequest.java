package com.akai.aicreator.model.request;

import lombok.Data;

/**
 * 对话历史创建请求
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@Data
public class ChatHistoryCreateRequest {

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型（user/ai）
     */
    private String messageType;

    /**
     * 应用ID
     */
    private Long appId;
}