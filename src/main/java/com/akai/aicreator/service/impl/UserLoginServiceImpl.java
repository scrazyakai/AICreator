package com.akai.aicreator.service.impl;

import com.akai.aicreator.entity.UserLogin;
import com.akai.aicreator.mapper.UserLoginMapper;
import com.akai.aicreator.model.vo.LoginStatisticsVO;
import com.akai.aicreator.service.IUserLoginService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 用户登录/签到表 服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-10
 */
@Service
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLogin> implements IUserLoginService {
    private static final Object LOGIN_TODAY = "1";
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserLoginMapper userLoginMapper;
    private static final String DAILY_LOGIN_KEY = "daily_login:user:";
    @Override
    public boolean checkUserLoginToDay(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        String today = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String cacheKey = DAILY_LOGIN_KEY + userId +today;
        Object value = redisTemplate.opsForValue().get(cacheKey);
        return LOGIN_TODAY.equals(value);
    }

    @Override
    public LoginStatisticsVO getUserLoginStatistics(long userId) {
        // 获取用户连续登录天数
        Integer consecutiveDays = userLoginMapper.getConsecutiveLoginDays(userId);
        
        // 创建返回对象
        LoginStatisticsVO statisticsVO = new LoginStatisticsVO();
        statisticsVO.setCurLongestLoginDays(consecutiveDays != null ? consecutiveDays : 0);
        
        return statisticsVO;
    }
}
