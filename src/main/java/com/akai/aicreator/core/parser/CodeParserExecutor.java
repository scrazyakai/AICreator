package com.akai.aicreator.core.parser;

import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;

public class CodeParserExecutor {
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML-> new HtmlCodeParser().parseCode(codeContent);
            case MULTI_FILE -> new MultiFileCodeParser().parseCode(codeContent);
            default ->  throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持的代码类型");
        };
    }
}
