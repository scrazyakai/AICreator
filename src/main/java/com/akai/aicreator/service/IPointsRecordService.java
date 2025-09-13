package com.akai.aicreator.service;

import com.akai.aicreator.entity.PointsRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 积分变动记录 服务类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
public interface IPointsRecordService extends IService<PointsRecord> {

    boolean addPointsRecord(Long userId, Integer points, String description);
}
