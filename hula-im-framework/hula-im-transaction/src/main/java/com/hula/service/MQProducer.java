package com.hula.service;

import com.hula.annotation.SecureInvoke;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 发送mq工具类
 * @author nyh
 */
public class MQProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String topic, Object body) {
        rabbitTemplate.convertAndSend(topic, body);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     *
     * @param topic 主题
     * @param body 消息体
     */
    @SecureInvoke
    public void sendSecureMsg(String topic, Object body, Object key) {
        rabbitTemplate.convertAndSend(topic, body, message -> {
            message.getMessageProperties().setHeader("KEYS", key);
            return message;
        });
    }
}
