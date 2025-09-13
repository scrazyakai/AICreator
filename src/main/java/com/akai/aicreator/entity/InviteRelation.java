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
 * 邀请关系
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("invite_relation")
public class InviteRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邀请人用户id
     */
    private Long inviterId;

    /**
     * 被邀请人用户id
     */
    private Long inviteeId;

    /**
     * 邀请码
     */
    private String inviteCode;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
