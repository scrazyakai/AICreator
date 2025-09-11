package com.akai.aicreator.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分变动记录视图
 *
 */
@Data
public class PointsRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 积分变动数量
     */
    private Integer points;

    /**
     * 积分变动描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
