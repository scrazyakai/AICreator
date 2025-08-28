package com.akai.aicreator.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.akai.aicreator.common.BaseResponse;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.common.ResultUtils;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;
import com.akai.aicreator.model.enums.MessageTypeEnum;
import com.akai.aicreator.model.enums.UserRoleEnum;
import com.akai.aicreator.model.request.*;
import com.akai.aicreator.model.vo.AppInfoVO;
import com.akai.aicreator.service.IAppService;
import com.akai.aicreator.service.IChatHistoryService;
import com.akai.aicreator.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 应用 前端控制器
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@RestController
@RequestMapping("/app")
public class AppController {
    @Resource
    private IChatHistoryService chatHistoryService;

    @Resource
    private IAppService appService;

    @Resource
    private IUserService userService;
    /**
     * 应用部署
     *
     * @param appDeployRequest 部署请求
     * @return 部署 URL
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest) {
        if(appDeployRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"部署请求不能为空");
        }
        Long appId = appDeployRequest.getAppId();
        if(appId == null || appId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        }
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId);
        return ResultUtils.success(deployUrl);
    }

    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatToGenCode(@RequestParam Long appId,
                                      @RequestParam String message) {
        // 参数校验
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID无效");
        }
        if (StrUtil.isBlank(message)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        }
        // 获取当前登录用户
        // 调用服务生成代码（流式）
        Flux<String> contentFlux = appService.chatToGenCode(appId, message);
        long userId = StpUtil.getLoginIdAsLong();
        // 转换为 ServerSentEvent 格式
        StringBuilder aiResponseBuilder = new StringBuilder();
        return contentFlux
                .map(chunk -> {
                    // 收集AI响应内容
                    aiResponseBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // 流式响应完成后，添加AI消息到对话历史
                    String aiResponse = aiResponseBuilder.toString();
                    if (StrUtil.isNotBlank(aiResponse)) {
                        chatHistoryService.saveAiMessage(appId, aiResponse, userId);
                    }
                })
                .doOnError(error -> {
                    // 如果AI回复失败，也要记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.saveAiErrorMessage(appId, errorMessage, userId);
                });

    }


    /**
     * 创建应用
     */
    @PostMapping("/create")
    public BaseResponse<Long> createApp(@RequestBody AppCreateRequest appCreateRequest) {
        if (appCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        Long appId = appService.createApp(appCreateRequest, userId);
        return ResultUtils.success(appId);
    }

    /**
     * 用户修改自己的应用
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest) {
        if (appUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean result = appService.updateApp(appUpdateRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 用户删除自己的应用
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody Long appId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        Boolean result = appService.deleteApp(appId, userId);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID查看应用详情
     */
    @GetMapping("/get/{id}")
    public BaseResponse<AppInfoVO> getAppById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        AppInfoVO appInfoVO = appService.getAppById(id);
        return ResultUtils.success(appInfoVO);
    }

    /**
     * 分页查询自己的应用列表
     */
    @PostMapping("/my/page")
    public BaseResponse<Page<AppInfoVO>> pageMyApps(@RequestBody AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        Page<AppInfoVO> result = appService.pageMyApps(appQueryRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询精选应用列表
     */
    @PostMapping("/featured/page")
    public BaseResponse<Page<AppInfoVO>> pageFeaturedApps(@RequestBody AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        Page<AppInfoVO> result = appService.pageFeaturedApps(appQueryRequest);
        return ResultUtils.success(result);
    }

    // ========== 管理员接口 ==========

    /**
     * 管理员删除应用
     */
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody Long appId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问");
        }
        
        Boolean result = appService.deleteAppByAdmin(appId);
        return ResultUtils.success(result);
    }

    /**
     * 管理员更新应用
     */
    @PostMapping("/admin/update")
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        if (appAdminUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问");
        }
        
        Boolean result = appService.updateAppByAdmin(appAdminUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询应用列表
     */
    @PostMapping("/admin/page")
    public BaseResponse<Page<AppInfoVO>> pageAppsForAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问");
        }
        
        Page<AppInfoVO> result = appService.pageAppsForAdmin(appQueryRequest);
        return ResultUtils.success(result);
    }

    /**
     * 管理员查看应用详情
     */
    @GetMapping("/admin/get/{id}")
    public BaseResponse<AppInfoVO> getAppByIdForAdmin(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        if (!isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问");
        }
        
        AppInfoVO appInfoVO = appService.getAppByIdForAdmin(id);
        return ResultUtils.success(appInfoVO);
    }

    /**
     * 判断是否为管理员
     */
    private boolean isAdmin() {
        Long userId = StpUtil.getLoginIdAsLong();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return false;
        }
        String userRole = user.getUserRole();
        return userRole.equals(UserRoleEnum.ADMIN.getValue());
    }

}
