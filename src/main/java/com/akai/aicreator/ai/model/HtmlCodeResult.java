package com.akai.aicreator.ai.model;

import jdk.jfr.Description;
import lombok.Data;

/**
 * HtmlCodeResult类用于封装HTML代码及其描述信息
 * 使用@Data注解自动生成getter、setter、equals、hashCode和toString方法
 */
@Description("生成HTML文件的结果")
@Data
public class HtmlCodeResult {

    /**
     * 存储HTML代码的字符串字段
     */
    @Description("生成的HTML代码")
    private String HtmlCode;
    /**
     * 存储对HTML代码描述信息的字符串字段
     */
    @Description("对生成的HTML代码的描述信息")
    private String description;
}
