package com.akai.aicreator.core.codeSaver;

import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.ai.model.MultiFileCodeResult;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;

public class MultiCodeFileSaver extends CodeFileSaverTemplate<MultiFileCodeResult>{

    /**
     * 多文件代码保存器
     *
     * @author yupi
     */

        @Override
        public CodeGenTypeEnum getCodeType() {
            return CodeGenTypeEnum.MULTI_FILE;
        }

        @Override
        protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
            // 保存 HTML 文件
            writeToFile(baseDirPath, "index.html", result.getHtmlCode());
            // 保存 CSS 文件
            writeToFile(baseDirPath, "style.css", result.getCssCode());
            // 保存 JavaScript 文件
            writeToFile(baseDirPath, "script.js", result.getJsCode());
        }

        @Override
        protected void validateInput(MultiFileCodeResult result) {
            super.validateInput(result);
            // 至少要有 HTML 代码，CSS 和 JS 可以为空
            if (StrUtil.isBlank(result.getHtmlCode())) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
            }
        }

}
