package com.akai.aicreator.core;

import cn.hutool.json.JSONUtil;
import com.akai.aicreator.ai.AiCodeGeneratorService;
import com.akai.aicreator.ai.AiCodeGeneratorServiceFactory;
import com.akai.aicreator.ai.model.HtmlCodeResult;
import com.akai.aicreator.ai.model.MultiFileCodeResult;
import com.akai.aicreator.ai.model.message.AiResponseMessage;
import com.akai.aicreator.ai.model.message.ChatResponseMessage;
import com.akai.aicreator.ai.model.message.ToolExecutedMessage;
import com.akai.aicreator.ai.model.message.ToolRequestMessage;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.core.codeSaver.CodeFileSaverExecutor;
import com.akai.aicreator.core.parser.CodeParserExecutor;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AiCodeGeneratorFacade 类
 * 这是一个外观模式的实现，用于提供简化的接口来访问复杂的代码生成子系统
 * 该类可能整合了多种代码生成相关的功能，为客户端提供一个统一的入口点
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {
    @Resource
    AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * 统一入口，根据类型生成并保存代码
     * @param userMessage 用户输入的消息，用于生成代码的依据
     * @param codeGenTypeEnum 代码生成类型枚举，指定生成代码的类型
     * @return 返回生成的代码字符串，具体返回值需要根据方法实现确定
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum,Long appId){
        if(codeGenTypeEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型不能为空");
        }
        //根据appId获取应用实例Id
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,codeGenTypeEnum);
        return switch (codeGenTypeEnum){
            case HTML-> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHTMLCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult,CodeGenTypeEnum.HTML,appId);
            }
            case MULTI_FILE-> {
                MultiFileCodeResult mutiFileCodeResult = aiCodeGeneratorService.generateMutiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(mutiFileCodeResult,CodeGenTypeEnum.MULTI_FILE,appId);
            }
            default-> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }
        /**
         * 根据用户消息和代码生成类型生成并保存代码流
         * @param userMessage 用户输入的消息
         * @param codeGenTypeEnum 代码生成类型枚举
         * @return 返回一个Flux<String>类型的代码流
         * @throws BusinessException 当代码生成类型为空或不支持时抛出业务异常
         */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum,Long appId){
        if(codeGenTypeEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型不能为空");
        }
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum){
            case HTML-> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateHTMLCodeStream(userMessage);
                yield generateCodeStream(stringFlux, CodeGenTypeEnum.HTML,appId);
            }
            case MULTI_FILE-> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateMutiFileCodeStream(userMessage);
                yield generateCodeStream(stringFlux, CodeGenTypeEnum.MULTI_FILE,appId);
            }
            case VUE_PROJECT -> {
                TokenStream stringFlux = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield processTokenStream(stringFlux);
            }
            default-> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }


    private Flux<String> generateCodeStream(Flux<String> fluxStream,CodeGenTypeEnum codeType,Long appId){
        StringBuilder codeBuilder = new StringBuilder();
        return fluxStream.doOnNext(codeBuilder::append)
                .doOnComplete(()->{
            try {
                String completeCode = codeBuilder.toString();
                Object parseCode = CodeParserExecutor.executeParser(completeCode,codeType);
                File fileDir = CodeFileSaverExecutor.executeSaver(parseCode,codeType,appId);
            } catch (Exception e) {
                log.error("文件保存失败,详细错误信息:", e);
            }
        });
    }
    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        ChatResponseMessage chatResponseMessage = new ChatResponseMessage(response);
                        sink.next(JSONUtil.toJsonStr(chatResponseMessage));
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }

}
