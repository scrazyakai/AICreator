package com.akai.aicreator.service.impl;

import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.constant.PointsConstant;
import com.akai.aicreator.entity.InviteRelation;
import com.akai.aicreator.entity.PointsRecord;
import com.akai.aicreator.entity.UserPoints;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.UserPointsMapper;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.model.enums.DescriptionEnum;
import com.akai.aicreator.model.vo.UserPointsVO;
import com.akai.aicreator.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户积分 服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2025-09-05
 */
@Service
public class UserPointsServiceImpl extends ServiceImpl<UserPointsMapper, UserPoints> implements IUserPointsService {
    @Resource
    private IUserService userService;

    @Resource
    @Lazy
    private IPointsRecordService pointsRecordService;
    @Autowired
    private ApplicationContext applicationContext;
    
    private IInviteRelationService getInviteRelationService() {
        return applicationContext.getBean(IInviteRelationService.class);
    }
    
    private IPointsService getPointsService() {
        return applicationContext.getBean(IPointsService.class);
    }
    @Override
    public UserPointsVO getUserPoints(Long userId) {
        User user = userService.getById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        UserPoints userPoints = this.getById(userId);
        if(userPoints == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"userPoints为空");
        }
        UserPointsVO userPointsVO = new UserPointsVO();
        BeanUtils.copyProperties(userPoints,userPointsVO);
        return userPointsVO;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean InitPoints(long userId, String inviteCode) {
        User user = userService.getById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        QueryWrapper<UserPoints> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        UserPoints userPoint = this.getOne(queryWrapper);
        if(userPoint != null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"已经初始化过了!");
        }
        int pointsToAdd;
        int totalPointsToSet;
        String description;
        Long inviterId = null;
        //有邀请码注册积分是25+50
        if(inviteCode == null || inviteCode.isEmpty()){
            //普通注册
            pointsToAdd = PointsConstant.REGISTER_DEFAULT_POINTS;
            totalPointsToSet = PointsConstant.REGISTER_DEFAULT_POINTS;
            description = DescriptionEnum.REGISTER_REWARD.getText();
        }else{
            if(!getInviteRelationService().validateInviteCode(inviteCode)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"邀请码无效");
            }
            inviterId = getInviteRelationService().getInviterByCode(inviteCode);
            if(inviterId == null || inviterId <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"邀请码无效");
            }
            pointsToAdd = PointsConstant.InVITE_REGISTER_POINTS;
            totalPointsToSet = PointsConstant.InVITE_REGISTER_POINTS;
            description = DescriptionEnum.INVITE_REGISTER_REWARD.getText();
        }
        //创建用户积分信息
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userId);
        userPoints.setPoints(pointsToAdd);
        userPoints.setTotalPoints(totalPointsToSet);
        userPoints.setCreateTime(LocalDateTime.now());
        userPoints.setUpdateTime(LocalDateTime.now());
        boolean saveUserPoints = this.save(userPoints);
        if(!saveUserPoints){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建用户积分信息失败!");
        }
        //创建积分记录
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(pointsToAdd);
        pointsRecord.setDescription(description);
        pointsRecord.setCreateTime(LocalDateTime.now());
        boolean savePointsRecord = pointsRecordService.save(pointsRecord);
        if(!savePointsRecord){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建用户积分记录失败");
        }
        if(inviteCode != null && !inviteCode.isEmpty()&& inviterId != null){
            InviteRelation inviteRelation = new InviteRelation();
            inviteRelation.setInviterId(inviterId);
            inviteRelation.setInviteeId(userId);
            inviteRelation.setInviteCode(inviteCode);
            inviteRelation.setCreateTime(LocalDateTime.now());
            boolean saveInviteRelation = getInviteRelationService().save(inviteRelation);
            if (!saveInviteRelation) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建邀请关系失败!");
            }
            // 给邀请人奖励积分
            boolean addInviterPoints = getPointsService().addPoints(inviterId, PointsConstant.INVITE_FRIEND_POINTS,
                    DescriptionEnum.INVITE_FRIEND_REWARD.getText());
            if (!addInviterPoints) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "给邀请人添加积分失败!");
            }
        }
        return true;
    }
}
