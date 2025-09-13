package com.akai.aicreator.service.impl;

import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.constant.PointsConstant;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.UserPointsMapper;
import com.akai.aicreator.model.enums.DescriptionEnum;
import com.akai.aicreator.model.vo.UserPointsVO;
import com.akai.aicreator.service.IPointsRecordService;
import com.akai.aicreator.service.IPointsService;
import com.akai.aicreator.service.IUserPointsService;
import com.akai.aicreator.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
public class PointsServiceImpl implements IPointsService {
    @Resource
    private IUserPointsService userPointsService;
    @Resource
    private UserPointsMapper userPointsMapper;
    @Resource
    private IPointsRecordService pointsRecordService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    private static final String POINTS_CACHE_KEY = "points:user:";
    private static final String DAILY_LOGIN_KEY = "daily_login:user:";
    private static final int CACHE_EXPIRE_HOURS = 24;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPoints(Long userId, Integer points, String description) {
        if (userId == null || points == null || points <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //user_point表中积分增加
        int updated = userPointsMapper.addPoints(userId, points);
        if(updated == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新积分失败!");
        }
        boolean savePointsRecord = pointsRecordService.addPointsRecord(userId, points, description);
        if(!savePointsRecord){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加积分记录失败!");
        }
        // 更新Redis缓存
        updatePointsCache(userId, points, true);
        
        log.info("用户ID: {} 增加积分: {}, 描述: {}", userId, points, description);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reducePoints(Long userId, Integer points, String description) {
        if(userId == null || points == null || points <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        try {
            // 检查积分是否充足
            if(!checkPoints(userId, points)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"积分不足");
            }
            
            //user_point表中积分减少
            int updated = userPointsMapper.deductPoints(userId, points);
            if(updated == 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新积分失败!");
            }
            
            //扣减积分记录（记录负数表示扣减）
            boolean savePointsRecord = pointsRecordService.addPointsRecord(userId, -points, description);
            if(!savePointsRecord){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加积分记录失败!");
            }
            
            // 更新Redis缓存
            updatePointsCache(userId, points, false);
            
            log.info("用户ID: {} 扣减积分: {}, 描述: {}", userId, points, description);
            return true;
        } catch (Exception e) {
            log.error("用户ID: {} 扣减积分失败: {}, 描述: {}, 错误: {}", userId, points, description, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 每日登录积分奖励
     * @param userId
     * @return
     */
    @Override
    public boolean dailyLoginReward(long userId) {
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        long secondsToMidnight = Duration.between(now,
                now.toLocalDate().plusDays(1).atStartOfDay()).getSeconds();
        String cacheKey = DAILY_LOGIN_KEY + userId +today;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(cacheKey, "1", secondsToMidnight, TimeUnit.SECONDS);
        if(Boolean.TRUE.equals(success)){
            this.addPoints(userId, PointsConstant.DAILY_LOGIN_POINTS, DescriptionEnum.DAILY_LOGIN_REWARD.getText());
        }
        return Boolean.TRUE.equals(success);
    }

    @Override
    public boolean checkPoints(Long userId, Integer points){
        try {
            // 先从Redis缓存获取积分
            Integer cachedPoints = getPointsFromCache(userId);
            if (cachedPoints != null) {
                log.debug("从缓存获取用户ID: {} 积分: {}", userId, cachedPoints);
                return cachedPoints >= points;
            }
            
            // 缓存未命中，从数据库获取
            UserPointsVO userPointsVO = userPointsService.getUserPoints(userId);
            if(userPointsVO == null){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取用户积分失败!");
            }
            int curPoints = userPointsVO.getPoints();
            
            // 更新缓存
            updatePointsCache(userId, curPoints, true);
            
            log.debug("从数据库获取用户ID: {} 积分: {}", userId, curPoints);
            return curPoints >= points;
        } catch (Exception e) {
            log.error("检查用户ID: {} 积分失败: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 从Redis缓存获取用户积分
     */
    private Integer getPointsFromCache(Long userId) {
        try {
            String cacheKey = POINTS_CACHE_KEY + userId;
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                // 处理不同的数字类型
                if (cached instanceof Integer) {
                    return (Integer) cached;
                } else if (cached instanceof Long) {
                    return ((Long) cached).intValue();
                } else if (cached instanceof Number) {
                    return ((Number) cached).intValue();
                }
            }
        } catch (Exception e) {
            log.warn("从Redis获取用户ID: {} 积分缓存失败: {}", userId, e.getMessage());
        }
        return null;
    }
    
    /**
     * 更新Redis缓存中的积分
     * @param userId 用户ID
     * @param points 积分变化量
     * @param isAdd true为增加，false为扣减
     */
    private void updatePointsCache(Long userId, Integer points, boolean isAdd) {
        try {
            String cacheKey = POINTS_CACHE_KEY + userId;
            
            // 检查key是否存在，如果不存在则先设置初始值
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                // 如果key不存在，先设置初始值
                redisTemplate.opsForValue().set(cacheKey, 0);
            }
            
            if (isAdd) {
                // 增加积分
                redisTemplate.opsForValue().increment(cacheKey, points);
            } else {
                // 扣减积分
                redisTemplate.opsForValue().increment(cacheKey, -points);
            }
            
            // 设置过期时间
            redisTemplate.expire(cacheKey, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            
            log.debug("更新用户ID: {} 积分缓存: {} {}", userId, isAdd ? "+" : "-", points);
        } catch (Exception e) {
            log.warn("更新用户ID: {} 积分缓存失败: {}", userId, e.getMessage());
        }
    }
    
    /**
     * 清除用户积分缓存
     */
    public void clearPointsCache(Long userId) {
        try {
            String cacheKey = POINTS_CACHE_KEY + userId;
            redisTemplate.delete(cacheKey);
            log.debug("清除用户ID: {} 积分缓存", userId);
        } catch (Exception e) {
            log.warn("清除用户ID: {} 积分缓存失败: {}", userId, e.getMessage());
        }
    }
}
