package com.akai.aicreator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 积分变动记录
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("points_record")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 积分变动数量（正数为增加，负数为减少）
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
