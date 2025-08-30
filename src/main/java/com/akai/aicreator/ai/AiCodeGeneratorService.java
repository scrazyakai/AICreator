package com.akai.aicreator.ai;

import com.akai.aicreator.ai.model.HtmlCodeResult;
import com.akai.aicreator.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    /**
     * 生成HTML代码的方法
     * @param userMessage 用户输入的消息内容
     * @return 返回生成的HTML代码字符串
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHTMLCode(String userMessage);
    /**
     * 生成多文件代码的方法
     * 根据用户输入的消息生成相应的多文件代码
     * 该方法使用了系统提示，从指定资源文件中加载提示信息
     * @param userMessage 用户输入的消息，将作为生成代码的依据
     * @return 生成的多文件代码，以字符串形式返回
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMutiFileCode(String userMessage);

    /**
     * 生成流式HTML代码的方法
     * @param userMessage 用户输入的消息内容
     * @return 返回生成的HTML代码字符串
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHTMLCodeStream(String userMessage);
    /**
     * 生成流式多文件代码的方法
     * 根据用户输入的消息生成相应的多文件代码
     * 该方法使用了系统提示，从指定资源文件中加载提示信息
     * @param userMessage 用户输入的消息，将作为生成代码的依据
     * @return 生成的多文件代码，以字符串形式返回
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMutiFileCodeStream(String userMessage);
    /**
     * 生成 Vue 项目代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成过程的流式响应
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    TokenStream generateVueProjectCodeStream(@MemoryId long appId, @UserMessage String userMessage);

}
