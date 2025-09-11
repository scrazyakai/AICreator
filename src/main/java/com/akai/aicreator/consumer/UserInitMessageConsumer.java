package com.akai.aicreator.consumer;

import com.akai.aicreator.exception.BusinessException;
import com.akai.aicreator.model.dto.UserPointsInitMessage;
import com.akai.aicreator.service.IUserPointsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserInitMessageConsumer {
    @Resource
    private IUserPointsService userPointsService;
    @RabbitListener(queues = "user.points.init.queue")
    public void consume(UserPointsInitMessage userPointsInitMessage){
        log.info("接收到用户积分初始化消息: {}", userPointsInitMessage);

        if (userPointsInitMessage == null || userPointsInitMessage.getUserId() == null) {
            log.error("接收到无效的用户积分初始化消息: {}", userPointsInitMessage);
            return; // 丢弃无效消息
        }

        try {
            // 处理积分初始化
            boolean success = userPointsService.InitPoints(
                    userPointsInitMessage.getUserId(),
                    userPointsInitMessage.getInviteCode()
            );

            if (success) {
                log.info("用户积分初始化成功: userId={}, inviteCode={}",
                        userPointsInitMessage.getUserId(), userPointsInitMessage.getInviteCode());
            } else {
                log.error("用户积分初始化失败: userId={}, inviteCode={}",
                        userPointsInitMessage.getUserId(), userPointsInitMessage.getInviteCode());
                // 不抛出异常，避免无限重试
                return;
            }

        } catch (BusinessException e) {
            // 业务异常，记录日志但不重试
            log.error("用户积分初始化业务异常: userId={}, inviteCode={}, error={}",
                    userPointsInitMessage.getUserId(), userPointsInitMessage.getInviteCode(), e.getMessage());
            // 不抛出异常，避免无限重试
            return;
        } catch (Exception e) {
            log.error("处理用户积分初始化消息系统异常: userId={}, error={}",
                    userPointsInitMessage.getUserId(), e.getMessage(), e);
            // 系统异常才重试
            throw new RuntimeException("处理用户积分初始化消息系统异常", e);
        }
    }
}
