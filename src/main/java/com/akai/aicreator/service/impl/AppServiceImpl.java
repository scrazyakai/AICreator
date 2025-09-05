package com.akai.aicreator.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.ai.AiCodeGenTypeRoutingService;
import com.akai.aicreator.ai.AiCodeGeneratorService;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.constant.AppConstant;
import com.akai.aicreator.core.AiCodeGeneratorFacade;
import com.akai.aicreator.core.builer.VueProjectBuilder;
import com.akai.aicreator.core.handler.StreamHandlerExecutor;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.AppMapper;
import com.akai.aicreator.model.entity.App;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;
import com.akai.aicreator.model.request.AppAdminUpdateRequest;
import com.akai.aicreator.model.request.AppCreateRequest;
import com.akai.aicreator.model.request.AppQueryRequest;
import com.akai.aicreator.model.request.AppUpdateRequest;
import com.akai.aicreator.model.vo.AppInfoVO;
import com.akai.aicreator.service.IAppService;
import com.akai.aicreator.service.IChatHistoryService;
import com.akai.aicreator.service.IUserService;
import com.akai.aicreator.service.ScreenshotService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.ThrowableUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用 服务实现类
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements IAppService {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private IUserService userService;
    @Resource
    private IChatHistoryService chatHistoryService;
    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;
    @Resource
    private VueProjectBuilder vueProjectBuilder;
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;
    @Resource
    private AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message) {
        if(appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId不能为空");
        }
        if(message == null || message.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"message不能为空");
        }
        App app = this.getById(appId);
        if(app == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"应用不存在");
        }
        User loginUser = userService.getLoginUser();
        if(!loginUser.getId().equals(app.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限访问该应用");
        }
        CodeGenTypeEnum codeGenType = app.getCodeGenType();
        if(codeGenType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"应用编码生成类型不能为空");
        }
        long userId = StpUtil.getLoginIdAsLong();
//        chatHistoryService.saveUserMessage(appId,message,userId);
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenType, appId);
        return streamHandlerExecutor.doExecute(codeStream,chatHistoryService,appId,codeGenType);
    }

    @Override
    public String deployApp(Long appId) {
        //参数校验
        if(appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId不能为空");
        }
        User loginUser = userService.getLoginUser();
        //查询应用信息
        App app = this.getById(appId);
        if(app == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"应用不存在");
        }
        //权限校验
        if(!loginUser.getId().equals(app.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限访问该应用");
        }
        //检验部署码
        String deployKey = app.getDeployKey();
        if(StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        //获取代码生成类型
        CodeGenTypeEnum codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator +  sourceDirName;
        //检验文件是否存在
        File sourceDir = new File(sourceDirPath);
        if(!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"代码文件不存在,请先生成代码");
        }
        //复制文件到部署目录
        //检验Vue项目
        if(codeGenType == CodeGenTypeEnum.VUE_PROJECT){
            //构建vue项目
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            if(!buildSuccess){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"构建Vue项目失败");
            }
            File distDir = new File(sourceDirPath ,"dist");
            if(!distDir.exists()){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"构建Vue项目成功，但未生成dist打包文件");
            }
            sourceDir = distDir;
            log.info("Vue项目构建成功,将部署在:{}",distDir.getAbsolutePath());
        }
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator +  deployKey;

        //复制文件到部署文件夹中
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"代码部署失败" + e);
        }
        String deployURL = String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
        generateAppScreenshotAsync(appId,deployURL);
        //更新应用的developKey和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        if(!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新应用信息失败");
        }
        //返回部署信息
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }
    @Resource
    private ScreenshotService screenshotService;

    /**
     * 异步生成应用截图并更新封面
     *
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    @Override
    public void generateAppScreenshotAsync(Long appId, String appUrl) {
        // 使用虚拟线程异步执行
        Thread.startVirtualThread(() -> {
            // 调用截图服务生成截图并上传
            String screenshotUrl = screenshotService.generateScreenshotAndUpload(appUrl);
            // 更新应用封面字段
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(screenshotUrl);
            boolean updated = this.updateById(updateApp);
            if(!updated) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新应用封面失败");
            }
        });
    }


    @Override
    public Long createApp(AppCreateRequest appCreateRequest, Long userId) {
        if (appCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        String initPrompt = appCreateRequest.getInitPrompt();
        appCreateRequest.setAppName(initPrompt.substring(0,5));
        String appName = appCreateRequest.getAppName();
        //CodeGenTypeEnum codeGenType = appCreateRequest.getCodeGenType();

        // 参数校验
        if (StrUtil.hasBlank(appName, initPrompt)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称和初始化Prompt不能为空");
        }
        
        if (appName.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能超过50个字符");
        }
        CodeGenTypeEnum codeGenType = aiCodeGenTypeRoutingService.routeCodeGenType(initPrompt);
        
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
        log.info("应用创建成功，类型为: {}", codeGenType.getValue());
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

        // 级联删除对话历史
        chatHistoryService.deleteChatHistoryByAppId(appId);

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
        // 级联删除对话历史
        chatHistoryService.deleteChatHistoryByAppId(appId);
        if(existApp.getDeployKey() != null){
            deleteFiles(appId,existApp.getDeployKey(),existApp.getCodeGenType());
        }
        return this.removeById(appId);
    }
    private void deleteFiles(long appId,String deployKey,CodeGenTypeEnum codeGenType){
        //部署路径
        String developPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        File file = new File(developPath);
        //删除部署文件
        if(file.exists()){
            FileUtil.del(file);
        }
        //生成文件路径
        String codeGenPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + codeGenType.getValue() + "_" + appId;
        File codeGenFile = new File(codeGenPath);
        if(codeGenFile.exists()){
            FileUtil.del(codeGenFile);
        }

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
        if(app.getCodeGenType()!=null){
            appInfoVO.setCodeGenType(app.getCodeGenType().getValue());
        }
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
                    if (app.getCodeGenType() != null) {
                        appInfoVO.setCodeGenType(app.getCodeGenType().getValue());
                    }
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
