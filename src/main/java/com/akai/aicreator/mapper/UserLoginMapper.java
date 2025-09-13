package com.akai.aicreator.mapper;

import com.akai.aicreator.entity.UserLogin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户登录/签到表 Mapper 接口
 * </p>
 *
 * @author Recursion
 * @since 2025-09-10
 */
public interface UserLoginMapper extends BaseMapper<UserLogin> {

    /**
     * 获取用户连续登录天数
     * @param userId 用户ID
     * @return 连续登录天数
     */
    Integer getConsecutiveLoginDays(@Param("userId") Long userId);

}
