package com.akai.aicreator.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateRequest {
    /**
     * 验证密码
     */
    private String password;
    /**
     * 新密码
     */
    private String newPassword;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;
}
