package com.akai.aicreator.service;

import com.akai.aicreator.entity.UserLogin;
import com.akai.aicreator.model.vo.LoginStatisticsVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户登录/签到表 服务类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-10
 */
public interface IUserLoginService extends IService<UserLogin> {
    /**
     * 检查用户今天是否已经登录了
     * @param userId
     * @return
     */
    boolean checkUserLoginToDay(Long userId);

    /**
     * 获取用户登录统计信息
     * @param userId
     * @return
     */
    LoginStatisticsVO getUserLoginStatistics(long userId);
}
