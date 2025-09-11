package com.akai.aicreator.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPointsVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 当前积分
     */
    private Integer points;

    /**
     * 累计获得积分
     */
    private Integer totalPoints;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 是否删除
     */
    private Integer isDelete;
}
