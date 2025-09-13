package com.akai.aicreator.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.entity.InviteRelation;
import com.akai.aicreator.entity.UserPoints;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.InviteRelationMapper;
import com.akai.aicreator.model.vo.UserPointsVO;
import com.akai.aicreator.service.IInviteRelationService;
import com.akai.aicreator.service.IUserPointsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 邀请关系 服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
@Service
public class InviteRelationServiceImpl extends ServiceImpl<InviteRelationMapper, InviteRelation> implements IInviteRelationService {
    @Autowired
    private ApplicationContext applicationContext;
    
    private IUserPointsService getUserPointsService() {
        return applicationContext.getBean(IUserPointsService.class);
    }
    @Override
    public boolean validateInviteCode(String inviteCode) {
        if(inviteCode == null || inviteCode.isEmpty()){
            return false;
        }
        QueryWrapper<InviteRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inviteCode",inviteCode);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Long getInviterByCode(String inviteCode) {
        if(inviteCode == null || inviteCode.isEmpty()){
            return 0L;
        }
        QueryWrapper<UserPoints> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inviteCode",inviteCode);
        UserPoints userPoints = getUserPointsService().getOne(queryWrapper);
        if(userPoints == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邀请用户不存在");
        }
        return userPoints.getUserId();
    }

    @Override
    public String genInviteCode(Long userId) {
        UserPointsVO userPointsVO = getUserPointsService().getUserPoints(userId);
        String inviteCode = userPointsVO.getInviteCode();
        if (inviteCode == null || inviteCode.isEmpty()) {
            inviteCode = RandomUtil.randomString(6);
            UserPoints updateUserPoints = new UserPoints();
            updateUserPoints.setInviteCode(inviteCode);
            QueryWrapper<UserPoints> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId",userId);
            getUserPointsService().update(updateUserPoints,queryWrapper);
        }
        return inviteCode;
    }

    @Override
    public String getInviteCode(long userId) {
        UserPointsVO userPointsVO = getUserPointsService().getUserPoints(userId);
        if(userPointsVO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        return userPointsVO.getInviteCode();
    }
}
