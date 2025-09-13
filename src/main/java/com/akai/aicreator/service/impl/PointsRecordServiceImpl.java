package com.akai.aicreator.service.impl;


import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.entity.PointsRecord;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.PointsRecordMapper;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.service.IPointsRecordService;
import com.akai.aicreator.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 积分变动记录 服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements IPointsRecordService {
    @Resource
    private IUserService userService;
    @Override
    public boolean addPointsRecord(Long userId, Integer points, String description) {
        if(userId == null || points == null || points == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在!");
        }
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(points);
        pointsRecord.setDescription(description);
        pointsRecord.setCreateTime(LocalDateTime.now());
        return this.save(pointsRecord);
    }
}
