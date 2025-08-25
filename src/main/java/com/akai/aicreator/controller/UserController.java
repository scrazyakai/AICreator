package com.akai.aicreator.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.UserInfo;
import com.akai.aicreator.common.BaseResponse;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.common.ResultUtils;
import com.akai.aicreator.entity.User;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.UserRoleEnum;
import com.akai.aicreator.model.request.*;
import com.akai.aicreator.model.vo.UserInfoVO;
import com.akai.aicreator.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author Recursion
 * @since 2025-08-25
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    IUserService userService;
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long userId = userService.userRegister(userAccount, password, checkPassword);
        return ResultUtils.success(userId);
    }
    @PostMapping("/login")
    public BaseResponse<UserInfoVO> userLogin(@RequestBody UserLoginRequest userLoginRequest){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if(StrUtil.hasBlank(userAccount,password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空!");
        }
        User user = userService.userLogin(userAccount, password);
        StpUtil.checkDisable(user.getId());
        StpUtil.login(user.getId());
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user,userInfoVO);
        return ResultUtils.success(userInfoVO);
    }
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(){
        try {
            StpUtil.logout();
            return ResultUtils.success(true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"退出失败!");
        }
    }
    @PostMapping("/getvo")
    public BaseResponse<UserInfoVO> getUserInfo(){
        Long loginId = StpUtil.getLoginIdAsLong();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",loginId);
        User user = userService.getOne(queryWrapper);
        if(user == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未登录");
        }
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user,userInfo);
        return ResultUtils.success(userInfo);
    }
    @PostMapping("/ban")
    public BaseResponse<Boolean> banUser(@RequestBody BanRequest banRequest){
        if(banRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        if(!isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Long bannerId = banRequest.getUserId();
        Long time = banRequest.getTime();
        if(StpUtil.isLogin(bannerId)){
            StpUtil.kickout(bannerId);
        }
        StpUtil.disable(bannerId,time);
        return ResultUtils.success(true);
    }
    @PostMapping("/unban")
    public BaseResponse<Boolean> unban(@RequestBody UnbanRequest unbanRequest){
        if(unbanRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        if(!isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        StpUtil.untieDisable(unbanRequest.getUserId());
        return ResultUtils.success(true);
    }
    @PostMapping("/updateInfo")
    public BaseResponse<Boolean> updateUser(@RequestBody UpdateRequest updateRequest){
        if(updateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        //获取当前用户Id
        return ResultUtils.success(userService.updateUser(updateRequest,false));
    }
    @PostMapping("/updatePassword")
    public BaseResponse<Boolean> updateUserPassword(@RequestBody UpdateRequest updateRequest){
        if(updateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
        }
        //获取当前用户Id
        Boolean success = userService.updateUser(updateRequest, true);
        Long userId = StpUtil.getLoginIdAsLong();
        StpUtil.kickout(userId);
        return ResultUtils.success(success);
    }
    private boolean isAdmin(){
        //获取当前用户Id
        Long userId = StpUtil.getLoginIdAsLong();
        //判断是否为管理员
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        User user = userService.getOne(queryWrapper);
        String userRole = user.getUserRole();
        return userRole.equals(UserRoleEnum.ADMIN.getValue());
    }

}
