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

    /**
     * 根据应用ID和用户消息生成代码的响应式方法
     *
     * @param appId 应用程序的唯一标识符
     * @param message 用户输入的消息内容
     * @return 返回一个包含生成代码的Flux流，Flux是ReactiveX中的响应式流，可以异步返回多个结果
 */
    Flux<String> chatToGenCode(Long appId, String message);

    /**
     * 部署应用程序的方法
     *
     * @param appId 应用程序的唯一标识符ID
     * @return 返回一个String类型的值，可能是部署结果或相关信息
     */
    String deployApp(Long appId);

    /**
     * 异步生成应用截图的方法
     *
     * @param appId 应用的唯一标识符ID
     * @param appUrl 应用的URL地址，用于获取应用内容
     */
    public void generateAppScreenshotAsync(Long appId, String appUrl);

}
