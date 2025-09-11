package com.akai.aicreator.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.UserMapper;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.model.enums.UserRoleEnum;
import com.akai.aicreator.model.request.UpdateRequest;
import com.akai.aicreator.model.request.UserRegisterRequest;
import com.akai.aicreator.model.vo.UserInfoVO;
import com.akai.aicreator.service.IUserService;
import com.akai.aicreator.service.UserPointsMessageProducer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author Recursion
 * @since 2025-08-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserPointsMessageProducer userPointsMessageProducer;
    @Override
    public User getLoginUser() {
        long userId = StpUtil.getLoginIdAsLong();
        return getById(userId);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userRegister(String account,String password,String checkPassword) {
        return userRegister(account,password,checkPassword,null);
    }

    @Transactional(rollbackFor = Exception.class)
    public long userRegister(String account,String password,String checkPassword,String inviteCode) {
        //校验
        if(StrUtil.hasBlank(account,password,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(account.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号太短");
        }
        if(password.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        if(!password.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】'；：\"\"'。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不符合规定");
        }
        //检查是否已经存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",account);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该账号已存在");
        }
        //插入数据
        String safePassword = getSafePassword(password);
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(safePassword);
        String suffix = "" + UUID.randomUUID();
        String userName = "用户" + suffix;
        user.setUserName(userName);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(user);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建用户失败");
        }
        try {
            userPointsMessageProducer.sendUserPointsInitMessage(user.getId(), inviteCode);
        } catch (Exception e) {
            System.err.println("发送用户积分初始化消息失败: " + e.getMessage());
        }
        return user.getId();
    }

    @Override
    public String getSafePassword(String password) {
        final String SALE = "akai";
        return DigestUtil.md5Hex((SALE + password).getBytes());
    }

    @Override
    public User userLogin(String userAccount, String password) {
        //校验
        if(StrUtil.hasBlank(userAccount,password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空!");
        }
        if (userAccount.length() < 4|| password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码不合法");
        }
        //加密
        String safePassword = getSafePassword(password);
        //匹配
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount).eq("userPassword",safePassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码错误");
        }
        return user;
    }

    @Override
    public Boolean updateUser(UpdateRequest updateRequest,boolean updatePassword) {
        if(updateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        //获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        //获取当前用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        User user = this.getOne(queryWrapper);
        String userName = updateRequest.getUserName();
        if(updatePassword){
            String password = updateRequest.getPassword();
            String newPassword = updateRequest.getNewPassword();
            String safePassword = getSafePassword(password);
            if(!user.getUserPassword().equals(safePassword)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"密码错误");
            }
            if(newPassword.length() < 8){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
            }
            String newSafePassword = getSafePassword(newPassword);
            if(safePassword.equals(newSafePassword)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"新密码不能与旧密码一致");
            }
            user.setUserPassword(newSafePassword);
        }
        if(userName != null){
            user.setUserName(userName);
        }
        String userAvatar = updateRequest.getUserAvatar();
        if(userAvatar != null){
            user.setUserAvatar(userAvatar);
        }
        String userProfile = updateRequest.getUserProfile();
        if(userProfile != null){
            user.setUserProfile(userProfile);
        }
        boolean save = updateById(user);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return save;
    }
}
