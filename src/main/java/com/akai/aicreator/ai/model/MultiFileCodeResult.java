package com.akai.aicreator.ai.model;

import jdk.jfr.Description;
import lombok.Data;

/**
 * 多文件代码结果类
 * 用于存储HTML、CSS、JS代码及描述信息
 */
@Description("多文件代码结果类")
@Data  // 使用Lombok注解自动生成getter、setter等方法
public class MultiFileCodeResult {
    @Description("HTML代码，用于存储前端页面的HTML结构代码")
    // HTML代码，用于存储前端页面的HTML结构代码
    private String HtmlCode;
    // CSS代码，用于存储前端页面的样式表代码
    @Description("CSS代码，用于存储前端页面的样式表代码")
    private String CssCode;
    // JavaScript代码，用于存储前端页面的交互逻辑代码
    @Description("JavaScript代码，用于存储前端页面的交互逻辑代码")
    private String JsCode;
    // 描述信息，用于存储对代码功能的说明或描述
    @Description("描述信息，用于存储对代码功能的说明或描述")
    private String description;
}
