package com.akai.aicreator.utils;

import com.akai.aicreator.constant.PointsConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
@Slf4j
@Component
public class TokenCalUtil {
    /**
     * 基础兑换比例：1积分 = 1000个Token
     */
    private static final BigDecimal BASE_POINTS_TO_TOKEN_RATIO = new BigDecimal(PointsConstant.POINTS_TO_TOKEN_RATIO);

    /**
     * 最小积分单位
     */
    private static final BigDecimal MIN_POINTS_UNIT = new BigDecimal("0.01");
    /**
     * 计算AI调用需要消耗的积分
     *
     * @param tokenCount Token数量
     * @return 需要消耗的积分（向上取整到0.01）
     */
    public BigDecimal calculatePointsToConsume(Integer tokenCount) {
        if (tokenCount == null || tokenCount <= 0) {
            return BigDecimal.ZERO;
        }

        // 计算积分：tokenCount / 1000
        BigDecimal points = new BigDecimal(tokenCount)
                .divide(BASE_POINTS_TO_TOKEN_RATIO, 4, RoundingMode.HALF_UP);

        // 向上取整到0.01
        points = points.divide(MIN_POINTS_UNIT, 0, RoundingMode.UP)
                .multiply(MIN_POINTS_UNIT);

        log.debug("Token计算：{}个Token = {}积分", tokenCount, points);
        return points;
    }

    /**
     * 计算AI调用需要消耗的积分（整数版本，保持向后兼容）
     *
     * @param tokenCount Token数量
     * @return 需要消耗的积分（整数）
     */
    public int calculatePointsToConsumeInt(Integer tokenCount) {
        BigDecimal points = calculatePointsToConsume(tokenCount);
        return points.intValue();
    }

    /**
     * 根据积分计算可用的Token数量
     *
     * @param points 积分数量
     * @return 可用的Token数量
     */
    public int calculateAvailableTokens(BigDecimal points) {
        if (points == null || points.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal tokens = points.multiply(BASE_POINTS_TO_TOKEN_RATIO);
        return tokens.intValue();
    }
    /**
     * 估算文本的Token数量（简单估算）
     * 这是一个简化的估算方法，实际项目中应该使用更精确的Token计算
     *
     * @param text 文本内容
     * @return 估算的Token数量
     */
    public int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // 简单估算：中文字符按2个Token计算，英文字符按1个Token计算
        int chineseChars = 0;
        int englishChars = 0;

        for (char c : text.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                chineseChars++;
            } else if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                englishChars++;
            }
        }

        return chineseChars * 2 + englishChars;
    }

}
