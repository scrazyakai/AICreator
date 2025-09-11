package com.akai.aicreator.service.impl;

import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.constant.PointsConstant;
import com.akai.aicreator.entity.UserPoints;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.DescriptionEnum;
import com.akai.aicreator.model.vo.TokenUsageInfo;
import com.akai.aicreator.service.IPointsService;
import com.akai.aicreator.service.TokenUsageService;
import com.akai.aicreator.utils.TokenCalUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class TokenUsageServiceImpl implements TokenUsageService {
    @Resource
    private IPointsService pointsService;
    @Resource
    private TokenCalUtil tokenCalUtil;
    @Override
    public TokenUsageInfo extractorTokenUsage(ChatResponse chatResponse){
        if(chatResponse == null || chatResponse.metadata() == null){
            log.warn("ChatResponse或metadata为空，无法提取token使用情况");
            return TokenUsageInfo.empty();
        }
        TokenUsage tokenUsage = chatResponse.metadata().tokenUsage();
        if(tokenUsage == null){
            log.warn("TokenUsage为空，无法提取token使用情况");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"TokenUsage为空，无法提取token使用情况");
        }
        return TokenUsageInfo.builder()
                .inputTokens(tokenUsage.inputTokenCount())
                .outputTokens(tokenUsage.outputTokenCount())
                .totalTokens(tokenUsage.totalTokenCount())
                .build();
    }

    @Override
    public boolean processTokenUsageAndDeductPoints(Long userId, ChatResponse chatResponse) {
        if(chatResponse == null || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或ChatResponse为空");
        }
        TokenUsageInfo tokenUsageInfo = extractorTokenUsage(chatResponse);
        if(!tokenUsageInfo.ValidateTokenUsage()){
            log.warn("Token使用信息无效，用户ID: {}, TokenUsageInfo: {}", userId, tokenUsageInfo);
            return false;
        }
        //计算token消耗响应的积分
        int totalConsume = tokenUsageInfo.tokenCount();
        BigDecimal pointsToConsume = tokenCalUtil.calculatePointsToConsume(totalConsume);
        if(pointsToConsume.compareTo(BigDecimal.ZERO) <= 0){
            log.info("Token使用量为0，无需扣减积分，用户ID: {}", userId);
            return true;
        }
        // 转换为整数积分（向上取整）
        int points = pointsToConsume.setScale(0, RoundingMode.UP).intValue();
        log.info("用户ID: {} 使用Token: {}, 需要消耗积分: {}", userId, totalConsume, points);
        if(!pointsService.checkPoints(userId, points)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"积分不足");
        }
        // 扣减积分
        boolean reducePoints = pointsService.reducePoints(userId, points, DescriptionEnum.AI_CONSUME.getText());
        if(!reducePoints){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"扣减积分失败");
        }
        return true;

    }

}
