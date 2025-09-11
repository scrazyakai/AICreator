package com.akai.aicreator.service;


import com.akai.aicreator.model.vo.TokenUsageInfo;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public interface TokenUsageService {
    /**
     * 返回Token使用信息
     * @param chatResponse
     * @return
     */
    TokenUsageInfo extractorTokenUsage(ChatResponse chatResponse);

    /**
     * 调用AI并消耗Token
     * @param userId
     * @param chatResponse
     * @return
     */
    boolean processTokenUsageAndDeductPoints(Long userId, ChatResponse chatResponse);


}
