package com.akai.aicreator.service;

import com.akai.aicreator.entity.InviteRelation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 邀请关系 服务类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
public interface IInviteRelationService extends IService<InviteRelation> {
    /**
     * 验证邀请码是否有效
     * @param inviteCode
     * @return
     */
    boolean validateInviteCode(String inviteCode);
    /**
     * 根据邀请码获取邀请人id
     * @param inviteCode
     * @return
     */
    Long getInviterByCode(String inviteCode);

    /**
     * 生成邀请码
     * @param userId
     * @return
     */
    String genInviteCode(Long userId);

    String getInviteCode(long userId);
}
