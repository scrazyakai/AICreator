package com.akai.aicreator.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @PostConstruct
    public void init() {
        // 设置系统属性，允许反序列化所有类
        System.setProperty("spring.amqp.deserialization.trust.all", "true");
    }
    /**
     * 配置JSON消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        // 对于JSON转换器，不需要设置信任包，因为JSON是安全的
        return new Jackson2JsonMessageConverter();
    }
    /**
     * 配置RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        // 开启发送确认
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功: " + correlationData);
            } else {
                System.err.println("消息发送失败: " + correlationData + ", 原因: " + cause);
            }
        });
        return template;
    }

    /**
     * 配置监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // 设置并发消费者数量
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);
        return factory;
    }
    // 用户积分初始化相关队列配置
    @Bean
    public Queue userPointsInitQueue() {
        return new Queue("user.points.init.queue", true); // 持久化队列
    }

    @Bean
    public DirectExchange userPointsExchange() {
        return new DirectExchange("user.points.exchange", true, false); // 持久化交换机
    }

    @Bean
    public Binding userPointsInitBinding(Queue userPointsInitQueue, DirectExchange userPointsExchange) {
        return BindingBuilder.bind(userPointsInitQueue).to(userPointsExchange).with("user.points.init");
    }
}
