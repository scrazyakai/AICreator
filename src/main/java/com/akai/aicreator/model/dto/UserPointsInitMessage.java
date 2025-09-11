package com.akai.aicreator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户积分初始化消息DTO
 * 用于RabbitMQ消息传递
 *
 * @author Recursion
 * @since 2025-01-27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPointsInitMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 邀请码（可选）
     */
    private String inviteCode;
    
    /**
     * 消息创建时间
     */
    private Long timestamp;
    
    /**
     * 消息类型
     */
    private String messageType = "USER_POINTS_INIT";
    
    public UserPointsInitMessage(Long userId, String inviteCode) {
        this.userId = userId;
        this.inviteCode = inviteCode;
        this.timestamp = System.currentTimeMillis();
    }
}