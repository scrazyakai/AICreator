package com.akai.aicreator.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AiCodeGenTypeRoutingServiceFactory {
    
    // 直接注入需要的 ChatModel
    @Resource(name = "routingChatModelPrototype")
    private ChatModel routingChatModelPrototype;
    
    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService(){
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(routingChatModelPrototype)
                .build();
    }
}
