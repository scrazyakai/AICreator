package com.akai.aicreator.service;

public interface IPointsService {
    /**
     * 增加积分
     * @return
     */
    boolean addPoints(Long userId, Integer points,  String description);
    /**
     * 扣除积分
     * @return
     */
    boolean reducePoints(Long userId, Integer points, String description);
    /**
     * 每日登录奖励
     * @return userId
     */
    boolean dailyLoginReward(long userId);

    /**
     * 查看积分是否充足
     * @param userId
     * @param points
     * @return
     */
    boolean checkPoints(Long userId, Integer points);

}
