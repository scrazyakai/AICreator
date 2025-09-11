package com.akai.aicreator.service;

import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.model.request.UpdateRequest;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author Recursion
 * @since 2025-08-25
 */
public interface IUserService extends IService<User> {
    /**
     * 用户注册
     * @param account
     * @param password
     * @param checkPassword
     * @return
     */
    long userRegister(String account,String password,String checkPassword);
    /**
     * 用户注册支持邀请码
     * @param account
     * @param password
     * @param checkPassword
     * @return
     */
    long userRegister(String account,String password,String checkPassword,String inviteCode);

    /**
     * 对密码加密
     * @param password
     * @return
     */
    String getSafePassword(String password);

    /**
     * 用户登录
     * @param userAccount
     * @param password
     * @return
     */
    User userLogin(String userAccount, String password);

    /**
     * 更新用户信息
     * @param updateRequest
     * @return
     */
    Boolean updateUser(UpdateRequest updateRequest,boolean updatePassword);
    User getLoginUser();
}
