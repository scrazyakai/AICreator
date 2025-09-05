package com.akai.aicreator.controller;

import com.akai.aicreator.constant.AppConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;

@RestController
@RequestMapping("/static")
public class StaticResourceController {
    private final String PREVIEW_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;
    @GetMapping("/{deployKey}/**")
    public ResponseEntity<Resource> getStaticResource(@PathVariable String deployKey, HttpServletRequest request){
        String resourcePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        resourcePath = resourcePath.substring(("/static/" + deployKey).length());
        if(resourcePath.isEmpty()){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", request.getRequestURI() + "/");
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
        if (resourcePath.equals("/")) {
            resourcePath = "/index.html";
        }
        // 构建文件路径
        String filePath = PREVIEW_ROOT_DIR + "/" + deployKey + resourcePath;
        File file = new File(filePath);
        // 检查文件是否存在
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        // 返回文件资源
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Type", getContentTypeWithCharset(filePath))
                .body(resource);

    }
    private String getContentTypeWithCharset(String filePath) {
        if (filePath.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filePath.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filePath.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
