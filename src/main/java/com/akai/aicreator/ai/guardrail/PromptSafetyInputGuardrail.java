package com.akai.aicreator.ai.guardrail;


import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PromptSafetyInputGuardrail implements InputGuardrail {
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "忽略之前的指令", "ignore previous instructions", "ignore above",
            "破解", "hack", "绕过", "bypass", "越狱", "jailbreak","显示源码", "show source code"
    );
    // 注入攻击模式
    private static final List<Pattern> INJECTION_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)ignore\\s+(?:previous|above|all)\\s+(?:instructions?|commands?|prompts?)"),
            Pattern.compile("(?i)(?:forget|disregard)\\s+(?:everything|all)\\s+(?:above|before)"),
            Pattern.compile("(?i)(?:pretend|act|behave)\\s+(?:as|like)\\s+(?:if|you\\s+are)"),
            Pattern.compile("(?i)system\\s*:\\s*you\\s+are"),
            Pattern.compile("(?i)new\\s+(?:instructions?|commands?|prompts?)\\s*:")
    );
    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String input = userMessage.singleText();
        if(input.length() > 1000){
            return fatal("输入内容过长");
        }
        if(input.trim().isEmpty()){
            return fatal("输入内容为空");
        }
        for (String word : SENSITIVE_WORDS){
            if(input.contains(word)){
                return fatal("输入内容包含敏感词汇");
            }
        }
        for (Pattern pattern : INJECTION_PATTERNS){
            if(pattern.matcher(input).find()){
                return fatal("输入内容包含注入攻击风险");
            }
        }
        return success();
    }


}
