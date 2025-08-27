package com.akai.aicreator.core.codeSaver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.akai.aicreator.common.ErrorCode;
import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaverTemplate<T> {
    private static final String FILE_ROOT_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "code_output";
    public final File saveCode(T result,Long appId){
        //校验输入
        validateInput(result);
        //统一文件路径
        String baseDirPath = buildUniqueDir(appId);
        //保存文件
        saveFiles(result,baseDirPath);
        //返回文件目录对象
        return new File(baseDirPath);
    }
    protected final void writeToFile(String baseDirPath,String fileName,String content){
        if(StrUtil.isNotBlank(content)){
            String filePath = baseDirPath + File.separator + fileName;
            FileUtil.writeString(content,filePath ,StandardCharsets.UTF_8);
        }
    }


    protected final String buildUniqueDir(Long appId){
        if(appId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId不能为空");
        }
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}",codeType, appId);
        String filePath =  FILE_ROOT_PATH+ File.separator + uniqueDirName;
        FileUtil.mkdir(filePath);
        return filePath;
    }
    protected  void validateInput(T result) {
        if(result == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
    }
    protected abstract CodeGenTypeEnum getCodeType();
    protected abstract void saveFiles(T result, String baseDirPath);
}
