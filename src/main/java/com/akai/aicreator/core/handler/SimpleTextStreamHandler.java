package com.akai.aicreator.core.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.akai.aicreator.model.enums.MessageTypeEnum;
import com.akai.aicreator.service.IChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器
 * 处理 HTML 和 MULTI_FILE 类型的流式响应
 */
@Slf4j
public class SimpleTextStreamHandler {

    /**
     * 处理传统流（HTML, MULTI_FILE）
     * 直接收集完整的文本响应
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux,
                               IChatHistoryService chatHistoryService,
                               long appId) {
        long userId = StpUtil.getLoginIdAsLong();
        StringBuilder aiResponseBuilder = new StringBuilder();
        return originFlux
                .map(chunk -> {
                    // 收集AI响应内容
                    aiResponseBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // 流式响应完成后，添加AI消息到对话历史
                    String aiResponse = aiResponseBuilder.toString();
                    chatHistoryService.saveAiMessage(appId, aiResponse,  userId);
                })
                .doOnError(error -> {
                    // 如果AI回复失败，也要记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.saveAiErrorMessage(appId, errorMessage,  userId);
                });
    }
}
