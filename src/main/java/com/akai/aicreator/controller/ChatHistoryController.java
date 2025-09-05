package com.akai.aicreator.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.common.BaseResponse;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.common.ResultUtils;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.entity.ChatHistory;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.model.enums.UserRoleEnum;
import com.akai.aicreator.model.request.ChatHistoryCreateRequest;
import com.akai.aicreator.model.request.ChatHistoryQueryRequest;
import com.akai.aicreator.service.IChatHistoryService;
import com.akai.aicreator.service.IUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 对话历史 前端控制器
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Resource
    private IChatHistoryService chatHistoryService;

    @Resource
    private IUserService userService;

    /**
     * 创建对话历史
     */
//    @PostMapping("/create")
//    public BaseResponse<Long> createChatHistory(@RequestBody ChatHistoryCreateRequest chatHistoryCreateRequest) {
//        if (chatHistoryCreateRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
//        }
//
//        Long userId = StpUtil.getLoginIdAsLong();
//        Long chatHistoryId = chatHistoryService.createChatHistory(chatHistoryCreateRequest, userId);
//        return ResultUtils.success(chatHistoryId);
//    }


    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> getLatestChatHistory(@PathVariable Long appId,
                                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                                  @RequestParam(required = false) DateTime lastCreateTime) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        Long currentUserId = StpUtil.getLoginIdAsLong();
        Page<ChatHistory> result = chatHistoryService.getLatestChatHistory(appId, currentUserId,pageSize,lastCreateTime);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询所有对话历史
     */
    @PostMapping("/admin/page")
    public BaseResponse<Page<ChatHistory>> pageChatHistoryForAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        if (chatHistoryQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问");
        }

        Page<ChatHistory> result = chatHistoryService.pageChatHistoryForAdmin(chatHistoryQueryRequest);
        return ResultUtils.success(result);
    }

    /**
     * 管理员删除对话历史
     */
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> deleteChatHistoryByAdmin(@RequestParam Long chatHistoryId) {
        if (chatHistoryId == null || chatHistoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对话历史ID不能为空");
        }

        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问");
        }

        Boolean result = chatHistoryService.deleteChatHistoryByAdmin(chatHistoryId);
        return ResultUtils.success(result);
    }

    /**
     * 判断是否为管理员
     */
    private boolean isAdmin() {
        User loginUser = userService.getLoginUser();
        return UserRoleEnum.ADMIN.getValue().equals(loginUser.getUserRole());
    }
}
