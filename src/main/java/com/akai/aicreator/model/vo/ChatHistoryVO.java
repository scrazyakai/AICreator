package com.akai.aicreator.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话历史信息VO
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@Data
public class ChatHistoryVO {
    
    /**
     * id
     */
    private Long id;

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

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 创建用户昵称
     */
    private String userName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}