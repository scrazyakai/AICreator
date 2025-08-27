package com.akai.aicreator.service.impl;

import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.AppMapper;
import com.akai.aicreator.model.entity.App;
import com.akai.aicreator.model.request.AppAdminUpdateRequest;
import com.akai.aicreator.model.request.AppCreateRequest;
import com.akai.aicreator.model.request.AppQueryRequest;
import com.akai.aicreator.model.request.AppUpdateRequest;
import com.akai.aicreator.model.vo.AppInfoVO;
import com.akai.aicreator.service.IAppService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用 服务实现类
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements IAppService {

    @Override
    public Long createApp(AppCreateRequest appCreateRequest, Long userId) {
        if (appCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        String appName = appCreateRequest.getAppName();
        String initPrompt = appCreateRequest.getInitPrompt();
        String codeGenType = appCreateRequest.getCodeGenType();
        
        // 参数校验
        if (StrUtil.hasBlank(appName, initPrompt)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称和初始化Prompt不能为空");
        }
        
        if (appName.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能超过50个字符");
        }
        
        // 创建应用
        App app = App.builder()
                .appName(appName)
                .initPrompt(initPrompt)
                .codeGenType(codeGenType)
                .userId(userId)
                .priority(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        boolean result = this.save(app);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建应用失败");
        }
        
        return app.getId();
    }

    @Override
    public Boolean updateApp(AppUpdateRequest appUpdateRequest, Long userId) {
        if (appUpdateRequest == null || appUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        Long appId = appUpdateRequest.getId();
        String appName = appUpdateRequest.getAppName();
        
        // 参数校验
        if (StrUtil.isBlank(appName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        }
        
        if (appName.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能超过50个字符");
        }
        
        // 校验应用存在和权限
        App existApp = this.getById(appId);
        if (existApp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        
        if (!existApp.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权修改该应用");
        }
        
        // 更新应用
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setAppName(appName);
        updateApp.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(updateApp);
    }

    @Override
    public Boolean updateAppByAdmin(AppAdminUpdateRequest appAdminUpdateRequest) {
        if (appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        Long appId = appAdminUpdateRequest.getId();
        String appName = appAdminUpdateRequest.getAppName();
        String cover = appAdminUpdateRequest.getCover();
        Integer priority = appAdminUpdateRequest.getPriority();
        
        // 校验应用存在
        App existApp = this.getById(appId);
        if (existApp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        
        // 更新应用
        App updateApp = new App();
        updateApp.setId(appId);
        
        if (StrUtil.isNotBlank(appName)) {
            if (appName.length() > 50) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能超过50个字符");
            }
            updateApp.setAppName(appName);
        }
        
        if (StrUtil.isNotBlank(cover)) {
            updateApp.setCover(cover);
        }
        
        if (priority != null) {
            updateApp.setPriority(priority);
        }
        
        updateApp.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(updateApp);
    }

    @Override
    public Boolean deleteApp(Long appId, Long userId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        // 校验应用存在和权限
        App existApp = this.getById(appId);
        if (existApp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        
        if (!existApp.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权删除该应用");
        }
        
        return this.removeById(appId);
    }

    @Override
    public Boolean deleteAppByAdmin(Long appId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        // 校验应用存在
        App existApp = this.getById(appId);
        if (existApp == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        
        return this.removeById(appId);
    }

    @Override
    public AppInfoVO getAppById(Long appId) {
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }
        
        App app = this.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        
        AppInfoVO appInfoVO = new AppInfoVO();
        BeanUtils.copyProperties(app, appInfoVO);
        return appInfoVO;
    }

    @Override
    public AppInfoVO getAppByIdForAdmin(Long appId) {
        return getAppById(appId);
    }

    @Override
    public Page<AppInfoVO> pageMyApps(AppQueryRequest appQueryRequest, Long userId) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        int current = appQueryRequest.getPageNum() != null && appQueryRequest.getPageNum() > 0 ? appQueryRequest.getPageNum() : 1;
        int size = appQueryRequest.getPageSize() != null && appQueryRequest.getPageSize() > 0 && appQueryRequest.getPageSize() <= 20 
                   ? appQueryRequest.getPageSize() : 10;
        
        // 构建查询条件
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        
        if (StrUtil.isNotBlank(appQueryRequest.getAppName())) {
            queryWrapper.like("appName", appQueryRequest.getAppName());
        }
        
        queryWrapper.orderByDesc("createTime");
        
        // 分页查询
        Page<App> appPage = this.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<AppInfoVO> voPage = new Page<>(current, size, appPage.getTotal());
        List<AppInfoVO> appInfoVOList = appPage.getRecords().stream()
                .map(app -> {
                    AppInfoVO appInfoVO = new AppInfoVO();
                    BeanUtils.copyProperties(app, appInfoVO);
                    return appInfoVO;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(appInfoVOList);
        return voPage;
    }

    @Override
    public Page<AppInfoVO> pageFeaturedApps(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        int current = appQueryRequest.getPageNum() != null && appQueryRequest.getPageNum() > 0 ? appQueryRequest.getPageNum() : 1;
        int size = appQueryRequest.getPageSize() != null && appQueryRequest.getPageSize() > 0 && appQueryRequest.getPageSize() <= 20 
                   ? appQueryRequest.getPageSize() : 10;
        
        // 构建查询条件（精选应用：优先级 > 0）
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("priority", 0);
        
        if (StrUtil.isNotBlank(appQueryRequest.getAppName())) {
            queryWrapper.like("appName", appQueryRequest.getAppName());
        }
        
        queryWrapper.orderByDesc("priority").orderByDesc("createTime");
        
        // 分页查询
        Page<App> appPage = this.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<AppInfoVO> voPage = new Page<>(current, size, appPage.getTotal());
        List<AppInfoVO> appInfoVOList = appPage.getRecords().stream()
                .map(app -> {
                    AppInfoVO appInfoVO = new AppInfoVO();
                    BeanUtils.copyProperties(app, appInfoVO);
                    return appInfoVO;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(appInfoVOList);
        return voPage;
    }

    @Override
    public Page<AppInfoVO> pageAppsForAdmin(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        
        int current = appQueryRequest.getPageNum() != null && appQueryRequest.getPageNum() > 0 ? appQueryRequest.getPageNum() : 1;
        int size = appQueryRequest.getPageSize() != null && appQueryRequest.getPageSize() > 0 ? appQueryRequest.getPageSize() : 10;
        
        // 构建查询条件
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        
        if (StrUtil.isNotBlank(appQueryRequest.getAppName())) {
            queryWrapper.like("appName", appQueryRequest.getAppName());
        }
        
        if (StrUtil.isNotBlank(appQueryRequest.getCover())) {
            queryWrapper.like("cover", appQueryRequest.getCover());
        }
        
        if (StrUtil.isNotBlank(appQueryRequest.getCodeGenType())) {
            queryWrapper.eq("codeGenType", appQueryRequest.getCodeGenType());
        }
        
        // 只有当priority大于0时才作为查询条件
        if (appQueryRequest.getPriority() != null && appQueryRequest.getPriority() > 0) {
            queryWrapper.eq("priority", appQueryRequest.getPriority());
        }
        
        // 只有当userId大于0时才作为查询条件
        if (appQueryRequest.getUserId() != null && appQueryRequest.getUserId() > 0) {
            queryWrapper.eq("userId", appQueryRequest.getUserId());
        }
        
        queryWrapper.orderByDesc("updateTime");
        
        // 分页查询
        Page<App> appPage = this.page(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<AppInfoVO> voPage = new Page<>(current, size, appPage.getTotal());
        List<AppInfoVO> appInfoVOList = appPage.getRecords().stream()
                .map(app -> {
                    AppInfoVO appInfoVO = new AppInfoVO();
                    BeanUtils.copyProperties(app, appInfoVO);
                    return appInfoVO;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(appInfoVOList);
        return voPage;
    }

}
