package com.akai.aicreator.service;

import com.akai.aicreator.entity.UserPoints;
import com.akai.aicreator.model.vo.UserPointsVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户积分 服务类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
public interface IUserPointsService extends IService<UserPoints> {
    /**
     * 注册账号初始化积分
     * @param userId
     * @return
     */
    public boolean InitPoints(long userId,String inviteCode);

    /**
     * 查看当前用户的积分
     * @param userId
     * @return
     */
    public UserPointsVO getUserPoints(Long userId);

}
