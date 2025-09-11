package com.akai.aicreator.core.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.service.IChatHistoryService;
import com.akai.aicreator.service.IPointsService;
import com.akai.aicreator.utils.TokenCalUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 简单文本流处理器
 * 处理 HTML 和 MULTI_FILE 类型的流式响应
 */
@Slf4j
@Component
public class SimpleTextStreamHandler {
    @Resource
    @Lazy
    private TokenCalUtil tokenCalUtil;
    @Resource
    @Lazy
    private IPointsService pointsService;
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
                    try {
                        int estimatedTokens = tokenCalUtil.estimateTokenCount(aiResponse);
                        BigDecimal pointsToConsume = tokenCalUtil.calculatePointsToConsume(estimatedTokens);
                        if(estimatedTokens > 0){
                            int points = pointsToConsume.setScale(0, RoundingMode.UP).intValue();
                            // 检查积分是否充足
                            if (pointsService.checkPoints(userId, points)) {
                                // 扣减积分
                                boolean success = pointsService.reducePoints(userId, points,
                                        com.akai.aicreator.model.enums.DescriptionEnum.AI_CONSUME.getText());
                                if (success) {
                                    log.info("用户ID: {} AI调用完成，基于估算Token({})扣减积分: {}", userId, estimatedTokens, points);
                                } else {
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"积分不足");
                                }
                            } else {
                                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"积分不足");
                            }
                        }
                    } catch (BusinessException e) {
                        log.error("用户ID: {} AI调用完成，积分扣减异常: {}", userId, e.getMessage(), e);
                        // 不抛出异常，避免影响流式响应
                    } catch (Exception e) {
                        log.error("用户ID: {} AI调用完成，积分扣减系统异常: {}", userId, e.getMessage(), e);
                        // 不抛出异常，避免影响流式响应
                    }
                })
                .doOnError(error -> {
                    // 如果AI回复失败，也要记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.saveAiErrorMessage(appId, errorMessage,  userId);
                    // AI调用失败时，不扣减积分
                    log.warn("用户ID: {} AI调用失败，不扣减积分", userId);
                });
    }
}
