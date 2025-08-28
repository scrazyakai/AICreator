package com.akai.aicreator.service;

import cn.hutool.ai.core.Message;
import cn.hutool.core.date.DateTime;
import com.akai.aicreator.model.entity.ChatHistory;
import com.akai.aicreator.model.request.ChatHistoryCreateRequest;
import com.akai.aicreator.model.request.ChatHistoryQueryRequest;
import com.akai.aicreator.model.vo.ChatHistoryVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.util.List;

/**
 * 对话历史 服务类
 *
 * @author <a href="https://github.com/scrazyakai">Recursion</a>
 */
public interface IChatHistoryService extends IService<ChatHistory> {

    /**
     * 创建对话历史
     * @param chatHistoryCreateRequest 创建请求
     * @param userId 用户ID
     * @return 对话历史ID
     */
    Long createChatHistory(ChatHistoryCreateRequest chatHistoryCreateRequest, Long userId);

    /**
     * 保存用户消息
     * @param appId 应用ID
     * @param message 消息内容
     * @param userId 用户ID
     * @return 对话历史ID
     */
    Long saveUserMessage(Long appId, String message, Long userId);

    /**
     * 保存AI消息
     * @param appId 应用ID
     * @param message 消息内容
     * @param userId 用户ID
     * @return 对话历史ID
     */
    Long saveAiMessage(Long appId, String message, Long userId);

    /**
     * 保存AI错误消息
     * @param appId 应用ID
     * @param errorMessage 错误消息
     * @param userId 用户ID
     * @return 对话历史ID
     */
    Long saveAiErrorMessage(Long appId, String errorMessage, Long userId);

    /**
     * 获取应用的最新对话历史（默认10条）
     * @param appId 应用ID
     * @param currentUserId 当前用户ID
     * @return 最新对话历史列表
     */
    Page<ChatHistory> getLatestChatHistory(Long appId, Long currentUserId, int pageSize, DateTime createTime);

    /**
     * 管理员分页查询所有对话历史
     * @param chatHistoryQueryRequest 查询请求
     * @return 分页结果
     */
    Page<ChatHistory> pageChatHistoryForAdmin(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 根据应用ID删除对话历史（级联删除）
     * @param appId 应用ID
     * @return 是否成功
     */
    Boolean deleteChatHistoryByAppId(Long appId);

    /**
     * 管理员删除对话历史
     * @param chatHistoryId 对话历史ID
     * @return 是否成功
     */
    Boolean deleteChatHistoryByAdmin(Long chatHistoryId);
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory messageWindowChatMemory,int maxCount);
}
