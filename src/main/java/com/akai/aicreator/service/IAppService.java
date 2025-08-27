package com.akai.aicreator.service;

import com.akai.aicreator.model.entity.App;
import com.akai.aicreator.model.request.AppCreateRequest;
import com.akai.aicreator.model.request.AppUpdateRequest;
import com.akai.aicreator.model.request.AppAdminUpdateRequest;
import com.akai.aicreator.model.request.AppQueryRequest;
import com.akai.aicreator.model.vo.AppInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务类
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
public interface IAppService extends IService<App> {

    /**
     * 创建应用
     * @param appCreateRequest 创建请求
     * @param userId 用户ID
     * @return 应用ID
     */
    Long createApp(AppCreateRequest appCreateRequest, Long userId);

    /**
     * 更新应用（用户）
     * @param appUpdateRequest 更新请求
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean updateApp(AppUpdateRequest appUpdateRequest, Long userId);

    /**
     * 更新应用（管理员）
     * @param appAdminUpdateRequest 管理员更新请求
     * @return 是否成功
     */
    Boolean updateAppByAdmin(AppAdminUpdateRequest appAdminUpdateRequest);

    /**
     * 删除应用（用户）
     * @param appId 应用ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteApp(Long appId, Long userId);

    /**
     * 删除应用（管理员）
     * @param appId 应用ID
     * @return 是否成功
     */
    Boolean deleteAppByAdmin(Long appId);

    /**
     * 根据ID获取应用详情
     * @param appId 应用ID
     * @return 应用详情
     */
    AppInfoVO getAppById(Long appId);

    /**
     * 根据ID获取应用详情（管理员）
     * @param appId 应用ID
     * @return 应用详情
     */
    AppInfoVO getAppByIdForAdmin(Long appId);

    /**
     * 分页查询用户自己的应用
     * @param appQueryRequest 查询请求
     * @param userId 用户ID
     * @return 分页结果
     */
    Page<AppInfoVO> pageMyApps(AppQueryRequest appQueryRequest, Long userId);

    /**
     * 分页查询精选应用
     * @param appQueryRequest 查询请求
     * @return 分页结果
     */
    Page<AppInfoVO> pageFeaturedApps(AppQueryRequest appQueryRequest);

    /**
     * 分页查询所有应用（管理员）
     * @param appQueryRequest 查询请求
     * @return 分页结果
     */
    Page<AppInfoVO> pageAppsForAdmin(AppQueryRequest appQueryRequest);
    Flux<String> chatToGenCode(Long appId, String message);
    String deployApp(Long appId);

}
