package com.akai.aicreator.controller;
import java.time.LocalDate;


import cn.dev33.satoken.stp.StpUtil;
import com.akai.aicreator.common.BaseResponse;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.common.ResultUtils;
import com.akai.aicreator.entity.UserLogin;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.vo.LoginStatisticsVO;
import com.akai.aicreator.service.IPointsService;
import com.akai.aicreator.service.IUserLoginService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户登录/签到表 前端控制器
 * </p>
 *
 * @author Recursion
 * @since 2025-09-10
 */
@RestController
@RequestMapping("/login")
public class UserLoginController {
    @Resource
    private IUserLoginService userLoginService;
    @Resource
    private IPointsService pointsService;

    /**
     * 每日登录
     * @return
     */
    @PostMapping("/everyday")
    @Transactional
    public BaseResponse<Boolean> userLogin() {
        long userId = StpUtil.getLoginIdAsLong();
        //查看今天是否已经登录
        boolean hasLogin  = userLoginService.checkUserLoginToDay(userId);
        if (hasLogin) {
            //true为已经登录
            return ResultUtils.success(true);
        }
        UserLogin userLogin = new UserLogin();
        userLogin.setUserId(userId);
        userLogin.setSignDate(LocalDate.now());
        userLogin.setCreateTime(LocalDate.now());
        boolean saveUserLogin = userLoginService.save(userLogin);
        if(!saveUserLogin){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"每日登录记录保存失败")
;        }
        boolean savePoints = pointsService.dailyLoginReward(userId);
        if(!savePoints) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "每日登录奖励发放失败");
        }
        //每日登录成功
        return ResultUtils.success(true);

    }

    /**
     * 检查今天是否已经登录
     * @return
     */
    @GetMapping("/check")
    public BaseResponse<Boolean> checkUserLoginToDay() {
        long userId = StpUtil.getLoginIdAsLong();
        return ResultUtils.success(userLoginService.checkUserLoginToDay(userId));
    }
    @PostMapping("/get/statistics")
    public BaseResponse<LoginStatisticsVO> getUserLoginStatistics() {
        long userId = StpUtil.getLoginIdAsLong();
        return ResultUtils.success(userLoginService.getUserLoginStatistics(userId));
    }
}
