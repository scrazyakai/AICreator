package com.akai.aicreator.core;

import com.akai.aicreator.ai.model.HtmlCodeResult;
import com.akai.aicreator.ai.model.MultiFileCodeResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CodeParserTest {

    @Test
    void parseHtmlCode() {
        String codeContent = """
                随便写一段描述：
                html 格式
                <!DOCTYPE html>
                <html>
                <head>
                    <title>测试页面</title>
                </head>
                <body>
                    <h1>Hello World!</h1>
                </body>
                </html>

                随便写一段描述
                """;
        HtmlCodeResult result = CodeParser.parseHtmlCode(codeContent);
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
    }

    @Test
    void parseMultiFileCode() {
        // 使用标准的代码块格式
        String codeContent = """
            创建一个完整的网页：
            
            html 格式
            ```html
            <!DOCTYPE html>
            <html>
            <head>
                <title>多文件示例</title>
                <link rel="stylesheet" href="style.css">
            </head>
            <body>
                <h1>欢迎使用</h1>
                <script src="script.js"></script>
            </body>
            </html>
            ```

            css 格式
            ```css
            h1 {
                color: blue;
                text-align: center;
            }
            ```

            js 格式
            ```js
            console.log('页面加载完成');
            ```
            """;

        MultiFileCodeResult result = CodeParser.parseMultiFileCode(codeContent);
        assertNotNull(result);
        assertNotNull(result.getHtmlCode());
        assertNotNull(result.getCssCode());
        assertNotNull(result.getJsCode());
    }

}
