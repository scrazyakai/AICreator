package com.akai.aicreator.ai;

import com.akai.aicreator.ai.guardrail.PromptSafetyInputGuardrail;
import com.akai.aicreator.ai.guardrail.RetryOutputGuardrail;
import com.akai.aicreator.ai.tools.*;
import com.akai.aicreator.config.ReasoningStreamingChatModelConfig;
import com.akai.aicreator.config.StreamingChatModelConfig;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;
import com.akai.aicreator.service.IChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private IChatHistoryService chatHistoryService;
    @Resource
    private ToolManager toolManager;
    
    // 注入 ApplicationContext 来动态获取 prototype Bean
    @Resource
    private ApplicationContext applicationContext;
    /**
     * 根据 appId 获取服务（为了兼容老逻辑）
     *
     * @param appId
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return getAiCodeGeneratorService(0);
    }
    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存键: {}, 原因: {}", key, cause);
            })
            .build();

    /**
     * 根据 appId 获取服务（带缓存）
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId,CodeGenTypeEnum codeGenTypeEnum) {
        String cacheKey = buildCacheKey(appId, codeGenTypeEnum);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenTypeEnum));
    }

    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenTypeEnum) {
        log.info("为 appId: {} 创建新的 AI 服务实例", appId);
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(50)
                .build();
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,20);
        return switch (codeGenTypeEnum){

            case HTML,MULTI_FILE ->{
                // 每次创建时获取新的 prototype 实例
                StreamingChatModel streamingChatModel = applicationContext.getBean("streamingChatModelPrototype", StreamingChatModel.class);
                yield  AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(streamingChatModel)
                        .chatMemory(chatMemory)
                        .inputGuardrails(new PromptSafetyInputGuardrail())
                        //.outputGuardrails(new RetryOutputGuardrail())影响前端流式输出
                        .build();
            }

            case VUE_PROJECT-> {
                // 每次创建时获取新的 prototype 实例
                StreamingChatModel reasoningStreamingChatModel = applicationContext.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools((Object) toolManager.getAllTools())
                        //解决未找到工具出现的问题
                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                ToolExecutionResultMessage.from(toolExecutionRequest,
                                        "Error: there is no tool called "+toolExecutionRequest.name())
                        )
                        .inputGuardrails(new PromptSafetyInputGuardrail())
                        //.outputGuardrails(new RetryOutputGuardrail()) 影响前端流式输出
                        .build();
            }

        };
    }
    /**
     * 构建缓存键的方法
     * @param appId 应用程序ID
     * @param codeGenTypeEnum 代码生成类型枚举
     * @return 返回由appId和codeGenTypeEnum组合而成的缓存键字符串
     */
    public String buildCacheKey(long appId, CodeGenTypeEnum codeGenTypeEnum){
    // 将appId和codeGenTypeEnum的值用下划线连接起来作为缓存键
        return appId+"_"+codeGenTypeEnum.getValue();
    }



}
