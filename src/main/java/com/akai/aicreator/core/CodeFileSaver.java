package com.akai.aicreator.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.ai.model.HtmlCodeResult;
import com.akai.aicreator.ai.model.MultiFileCodeResult;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;
@Deprecated
public class CodeFileSaver {
    //文件保存的根目录
    private static final String FILE_ROOT_PATH = System.getProperty("user.dir") + "/tmp/code_output";
    //保存HTML代码
    public static File saveHtmlCode(HtmlCodeResult htmlCodeResult) {
        //构建文件的唯一路径
        String baseDirPath =  buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        //保存文件
        writeToFile(baseDirPath,"index.html",htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }
    //保存多文件代码
    public static File saveMutiFileCode(MultiFileCodeResult mutiFileCodeResult) {
        //构建文件的唯一路径
        String baseDirPath =  buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        //保存文件
        writeToFile(baseDirPath,"index.html",mutiFileCodeResult.getHtmlCode());
        writeToFile(baseDirPath,"style.css",mutiFileCodeResult.getCssCode());
        writeToFile(baseDirPath,"script.js",mutiFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }
    //构建文件的唯一路径/tmp/code_output+雪花ID
    private static String buildUniqueDir(String bizType) {
        String uniqueDirPath = StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
        String filePath =  FILE_ROOT_PATH+ File.separator + uniqueDirPath;
        return filePath;
    }
    //写入单个文件
    private static void writeToFile(String dirPath,String fileName,String fileContent) {
        //构建文件的唯一路径
        String filePath =  dirPath+ File.separator + fileName;
        //保存文件
        FileUtil.writeString(fileContent,filePath, StandardCharsets.UTF_8);
    }
}
