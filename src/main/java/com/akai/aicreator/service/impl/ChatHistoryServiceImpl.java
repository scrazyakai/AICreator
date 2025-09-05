package com.akai.aicreator.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.mapper.ChatHistoryMapper;
import com.akai.aicreator.model.entity.App;
import com.akai.aicreator.model.entity.ChatHistory;
import com.akai.aicreator.model.entity.User;
import com.akai.aicreator.model.enums.MessageTypeEnum;
import com.akai.aicreator.model.request.ChatHistoryCreateRequest;
import com.akai.aicreator.model.request.ChatHistoryQueryRequest;
import com.akai.aicreator.model.vo.ChatHistoryVO;
import com.akai.aicreator.service.IAppService;
import com.akai.aicreator.service.IChatHistoryService;
import com.akai.aicreator.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对话历史 服务实现类
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements IChatHistoryService {
    @Lazy
    @Resource
    private IAppService appService;

    @Resource
    private IUserService userService;

    @Override
    public Long createChatHistory(ChatHistoryCreateRequest chatHistoryCreateRequest, Long userId) {
        if (chatHistoryCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        String message = chatHistoryCreateRequest.getMessage();
        String messageType = chatHistoryCreateRequest.getMessageType();
        Long appId = chatHistoryCreateRequest.getAppId();

        // 参数校验
        if (StrUtil.hasBlank(message, messageType) || appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容、消息类型和应用ID不能为空");
        }

        if (message.length() > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不能超过10000个字符");
        }

        // 校验消息类型
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getEnumByValue(messageType);
        if (messageTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息类型不正确");
        }

        // 校验应用存在
        App app = appService.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        // 创建对话历史
        ChatHistory chatHistory = ChatHistory.builder()
                .message(message)
                .messageType(messageTypeEnum)
                .appId(appId)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        boolean result = this.save(chatHistory);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建对话历史失败");
        }

        return chatHistory.getId();
    }

    @Override
    public Long saveUserMessage(Long appId, String message, Long userId) {
        return saveMessage(appId, message, MessageTypeEnum.USER, userId);
    }

    @Override
    public Long saveAiMessage(Long appId, String message, Long userId) {
        return saveMessage(appId, message, MessageTypeEnum.AI, userId);
    }

    @Override
    public Long saveAiErrorMessage(Long appId, String errorMessage, Long userId) {
        String formattedErrorMessage = "[错误] " + errorMessage;
        return saveMessage(appId, formattedErrorMessage, MessageTypeEnum.AI, userId);
    }

    /**
     * 保存消息的通用方法
     */
    private Long saveMessage(Long appId, String message, MessageTypeEnum messageType, Long userId) {
        if (appId == null || StrUtil.isBlank(message) || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 校验应用存在
        App app = appService.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        ChatHistory chatHistory = ChatHistory.builder()
                .message(message)
                .messageType(messageType)
                .appId(appId)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        boolean result = this.save(chatHistory);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存消息失败");
        }

        return chatHistory.getId();
    }

    @Override
    public Page<ChatHistory> getLatestChatHistory(Long appId, Long currentUserId,int pageSize,DateTime lastCreateTime) {
        if (appId == null || currentUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if(pageSize <= 0 || pageSize > 50){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "pageSize必须在0-50之间");
        }


        // 校验应用存在和权限
        App app = appService.getById(appId);
        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }

        User currentUser = userService.getById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

//        boolean isAdmin = "管理员".equals(currentUser.getUserRole()) || "admin".equals(currentUser.getUserRole());
//        if (!isAdmin && !app.getUserId().equals(currentUserId)) {
//            throw new BusinessException(ErrorCode.NO_AUTH, "无权查看该应用的对话历史");
//        }

        // 查询最新10条记录
        ChatHistoryQueryRequest chatHistoryQueryRequest = new ChatHistoryQueryRequest();
        chatHistoryQueryRequest.setAppId(appId);
//        chatHistoryQueryRequest.setUserId(currentUserId);
        chatHistoryQueryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper<ChatHistory> queryWrapper = this.buildQueryWrapper(chatHistoryQueryRequest);
        return this.page(new Page<>(1, pageSize, false), queryWrapper);
    }


    @Override
    public Boolean deleteChatHistoryByAppId(Long appId) {
        if (appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        }

        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("appId", appId);
        boolean remove = this.remove(queryWrapper);
        if(!remove){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return remove;
    }

    @Override
    public Boolean deleteChatHistoryByAdmin(Long chatHistoryId) {
        if (chatHistoryId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对话历史ID不能为空");
        }

        ChatHistory chatHistory = this.getById(chatHistoryId);
        if (chatHistory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "对话历史不存在");
        }

        return this.removeById(chatHistoryId);
    }

    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            // 构造查询条件，获取最新的消息（排除第一条）
            QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("appId", appId)
                    .orderByDesc("createTime")
                    .last("LIMIT 1, " + maxCount);

            List<ChatHistory> historyList = this.list(queryWrapper);
            if (CollUtil.isEmpty(historyList)) {
                return 0;
            }

            // 反转列表，确保按时间正序（老的在前，新的在后）
            Collections.reverse(historyList);

            // 按时间顺序添加到记忆中
            int loadedCount = 0;
            // 先清理历史缓存，防止重复加载
            chatMemory.clear();

            for (ChatHistory history : historyList) {
                if (MessageTypeEnum.USER.equals(history.getMessageType())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                    loadedCount++;
                } else if (MessageTypeEnum.AI.equals(history.getMessageType())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                    loadedCount++;
                }
            }

            log.info("成功为 appId: {} 加载了 {} 条历史对话", appId, loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史对话失败，appId: {}, error: {}", appId, e.getMessage(), e);
            // 加载失败不影响系统运行，只是没有历史上下文
            return 0;
        }
    }



    @Override
    public Page<ChatHistory> pageChatHistoryForAdmin(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        if (chatHistoryQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        // 获取分页参数
        int pageNum = chatHistoryQueryRequest.getPageNum() != null && chatHistoryQueryRequest.getPageNum() > 0 
                      ? chatHistoryQueryRequest.getPageNum() : 1;
        int pageSize = chatHistoryQueryRequest.getPageSize() != null && chatHistoryQueryRequest.getPageSize() > 0 
                       ? chatHistoryQueryRequest.getPageSize() : 10;

        // 构建查询条件
        QueryWrapper<ChatHistory> queryWrapper = buildQueryWrapper(chatHistoryQueryRequest);
        
        // 分页查询
        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 构建查询条件
     */
    private QueryWrapper<ChatHistory> buildQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();

        if (chatHistoryQueryRequest == null) {
            // 无参数时，只添加默认排序，查询全部
            queryWrapper.orderByDesc("createTime");
            return queryWrapper;
        }
        
        Long appId = chatHistoryQueryRequest.getAppId();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long userId = chatHistoryQueryRequest.getUserId();
        String message = chatHistoryQueryRequest.getMessage();
        DateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();

        // 只有当参数不为空且有效时才添加查询条件
        if(appId != null && appId > 0){
            queryWrapper.eq("appId", appId);
        }
        if(StrUtil.isNotBlank(messageType)){
            queryWrapper.eq("messageType", messageType);
        }
        if(userId != null && userId > 0){
            queryWrapper.eq("userId", userId);
        }
        if(StrUtil.isNotBlank(message)){
            queryWrapper.like("message", message);
        }
        if(lastCreateTime != null){
            queryWrapper.lt("createTime", lastCreateTime);
        }
        
        // 排序条件
        if (StrUtil.isNotBlank(sortField)) {
            boolean isAsc = "ascend".equalsIgnoreCase(sortOrder);
            queryWrapper.orderBy(true, isAsc, sortField);
        } else {
            // 默认按 create_time 降序
            queryWrapper.orderByDesc("createTime");
        }
        
        return queryWrapper;
    }


}
