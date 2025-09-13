package com.akai.aicreator.mapper;

import com.akai.aicreator.entity.UserPoints;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户积分 Mapper 接口
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
public interface UserPointsMapper extends BaseMapper<UserPoints> {
    /**
     * 增加用户积分（乐观锁版本）
     * @param userId 用户ID
     * @param points 增加的积分
     * @return 影响行数
     */
    @Update("UPDATE user_points SET points = points + #{points}, totalPoints = totalPoints + #{points}, updateTime = NOW() WHERE userId = #{userId} AND points >= 0")
    int addPoints(@Param("userId") Long userId, @Param("points") Integer points);

    /**
     * 扣减用户积分（乐观锁版本）
     * @param userId 用户ID
     * @param points 扣减的积分
     * @return 影响行数
     */
    @Update("UPDATE user_points SET points = points - #{points}, updateTime = NOW() WHERE userId = #{userId} AND points >= #{points}")
    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);
}
