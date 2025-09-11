package com.akai.aicreator.model.vo;

import lombok.Data;

@Data
@lombok.Builder
public class TokenUsageInfo {
    private Integer inputTokens;
    private Integer outputTokens;
    private Integer totalTokens;

    /**
     * 创建空的token使用情况
     * @return
     */
    public static TokenUsageInfo empty() {
        return TokenUsageInfo.builder()
                .inputTokens(0)
                .outputTokens(0)
                .totalTokens(0)
                .build();
    }
    public boolean ValidateTokenUsage(){
        return totalTokens != null && totalTokens > 0;
    }
    public int tokenCount(){
        if(totalTokens != null && totalTokens > 0){
            return totalTokens;
        }
        int input = inputTokens !=null ? inputTokens : 0;
        int output = outputTokens != null ? outputTokens : 0;
        return input + output;

    }
    @Override
    public String toString() {
        return String.format("TokenUsageInfo{inputTokens=%d, outputTokens=%d, totalTokens=%d}",
                inputTokens, outputTokens, totalTokens);
    }

}
