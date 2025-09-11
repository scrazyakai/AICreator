package com.akai.aicreator.service;

import com.akai.aicreator.model.dto.UserPointsInitMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserPointsMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    public void sendUserPointsInitMessage(Long userId,String inviteCode) {
        try {
            UserPointsInitMessage message = new UserPointsInitMessage(userId, inviteCode);
            rabbitTemplate.convertAndSend("user.points.exchange", "user.points.init", message);
            log.info("用户积分初始化消息发送成功: userId={}, inviteCode={}", userId, inviteCode);
        } catch (AmqpException e) {
            log.error("发送用户积分初始化消息失败: userId={}, inviteCode={}, error={}",
                    userId, inviteCode, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    public void sendUserPointsInitMessage(Long userId) {
        sendUserPointsInitMessage(userId, null);
    }
}
