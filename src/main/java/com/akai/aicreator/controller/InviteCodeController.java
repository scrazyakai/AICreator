package com.akai.aicreator.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.akai.aicreator.common.BaseResponse;
import com.akai.aicreator.common.ResultUtils;
import com.akai.aicreator.service.IInviteRelationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inviteCode")
public class InviteCodeController {
    @Resource
    private IInviteRelationService inviteRelationService;
    @PostMapping("/create")
    public BaseResponse<String> createInviteCode(){
        long userId = StpUtil.getLoginIdAsLong();
        String inviteCode = inviteRelationService.genInviteCode(userId);
        return ResultUtils.success(inviteCode);
    }
    @GetMapping("/get")
    public BaseResponse<String> getInviteCode(){
        long userId = StpUtil.getLoginIdAsLong();
        String inviteCode = inviteRelationService.getInviteCode(userId);
        return ResultUtils.success(inviteCode);
    }
}
