package com.akai.aicreator;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.akai.aicreator",exclude = {RedisEmbeddingStoreAutoConfiguration.class})

@EnableAspectJAutoProxy
@MapperScan("com.akai.aicreator.mapper")
public class AiCreatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCreatorApplication.class, args);
    }

}
