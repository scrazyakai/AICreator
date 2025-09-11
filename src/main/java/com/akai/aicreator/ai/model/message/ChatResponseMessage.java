package com.akai.aicreator.ai.model.message;

import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatResponseMessage extends StreamMessage {
    private ChatResponse chatResponse;
    public ChatResponseMessage(ChatResponse chatResponse) {
        super("chat_response");
        this.chatResponse = chatResponse;
    }
}