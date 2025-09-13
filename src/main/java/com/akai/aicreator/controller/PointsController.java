package com.akai.aicreator.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.akai.aicreator.common.BaseResponse;
import com.akai.aicreator.common.ResultUtils;
import com.akai.aicreator.entity.PointsRecord;
import com.akai.aicreator.model.vo.PointsRecordVO;
import com.akai.aicreator.model.vo.UserPointsVO;
import com.akai.aicreator.service.IInviteRelationService;
import com.akai.aicreator.service.IPointsRecordService;
import com.akai.aicreator.service.IPointsService;
import com.akai.aicreator.service.IUserPointsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 用户积分 前端控制器
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
@RestController
@RequestMapping("/points")
public class PointsController {
    @Resource
    private IUserPointsService userPointsService;
    @Resource
    private IPointsService pointsService;
    @Resource
    private IPointsRecordService pointsRecordService;
    @Resource
    private IInviteRelationService inviteRelationService;
    @GetMapping("/get")
    public BaseResponse<UserPointsVO> getPoints() {
        long userId = StpUtil.getLoginIdAsLong();
        UserPointsVO userPoints = userPointsService.getUserPoints(userId);
        return ResultUtils.success(userPoints);
    }
    @PostMapping("/daily")
    public BaseResponse<Boolean> dailyLoginReward(){
        long userId = StpUtil.getLoginIdAsLong();
        boolean success = pointsService.dailyLoginReward(userId);
        return ResultUtils.success(success);
    }
    /**
     * 获取积分记录（分页）
     */
    @GetMapping("/records")
    public BaseResponse<Page<PointsRecord>> getRecords(@RequestParam(defaultValue = "1") int current,
                                                       @RequestParam(defaultValue = "10") int pageSize){
        long userId = StpUtil.getLoginIdAsLong();
        QueryWrapper<PointsRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId).orderByDesc("createTime");
        Page<PointsRecord> pages = pointsRecordService.page(new Page<PointsRecord>(current, pageSize), queryWrapper);
        return ResultUtils.success(pages);

    }
}
