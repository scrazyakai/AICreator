package com.akai.aicreator.core;

import com.akai.aicreator.ai.AiCodeGeneratorService;
import com.akai.aicreator.ai.model.HtmlCodeResult;
import com.akai.aicreator.ai.model.MultiFileCodeResult;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * AiCodeGeneratorFacade 类
 * 这是一个外观模式的实现，用于提供简化的接口来访问复杂的代码生成子系统
 * 该类可能整合了多种代码生成相关的功能，为客户端提供一个统一的入口点
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {
    @Resource
    AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口，根据类型生成并保存代码
     * @param userMessage 用户输入的消息，用于生成代码的依据
     * @param codeGenTypeEnum 代码生成类型枚举，指定生成代码的类型
     * @return 返回生成的代码字符串，具体返回值需要根据方法实现确定
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum ){
        if(codeGenTypeEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型不能为空");
        }
       return switch (codeGenTypeEnum){
            case HTML->  generateHtmlCode(userMessage);
            case MULTI_FILE-> generateMultiFileCode(userMessage);
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
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum ){
        if(codeGenTypeEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码生成类型不能为空");
        }
        return switch (codeGenTypeEnum){
            case HTML->  generateHtmlCodeStream(userMessage);
            case MULTI_FILE-> generateMultiFileCodeStream(userMessage);
            default-> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }



    private Flux<String> generateHtmlCodeStream(String userMessage) {
        Flux<String> stringFlux = aiCodeGeneratorService.generateHTMLCodeStream(userMessage);
        //定义拼接器
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux.doOnNext(codeBuilder::append).doOnComplete(() -> {
            // 保存代码到文件
            try {
                String completeHtmlCode = codeBuilder.toString();
                HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
                File fileDir = CodeFileSaver.saveHtmlCode(htmlCodeResult);
                log.info("文件保存成功,目录为:{}", fileDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("文件保存失败:{}", e.getMessage());
            }
        });
    }
    private Flux<String> generateMultiFileCodeStream(String userMessage) {
        Flux<String> stringFlux = aiCodeGeneratorService.generateMutiFileCodeStream(userMessage);
        //定义拼接器
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux.doOnNext(codeBuilder::append).doOnComplete(() -> {
            // 保存代码到文件
            try {
                String completeMultiCode = codeBuilder.toString();
                MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeMultiCode);
                File fileDir = CodeFileSaver.saveMutiFileCode(multiFileCodeResult);
                log.info("文件保存成功,目录为:{}", fileDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("文件保存失败:{}", e.getMessage());
            }
        });
    }

    private File generateMultiFileCode(String userMessage) {
        MultiFileCodeResult mutiFileCodeResult = aiCodeGeneratorService.generateMutiFileCode(userMessage);
        return CodeFileSaver.saveMutiFileCode(mutiFileCodeResult);

    }

    private File generateHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHTMLCode(userMessage);
        return CodeFileSaver.saveHtmlCode(htmlCodeResult);
    }
}
