package com.akai.aicreator.model.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String password;
    /**
     * 确认密码
     */
    private String checkPassword;
    /**
     * 邀请码
     */
    private String inviteCode;
}
