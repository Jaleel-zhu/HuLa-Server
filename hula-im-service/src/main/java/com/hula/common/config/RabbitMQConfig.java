package com.hula.common.config;

import com.hula.common.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自定义RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // 消息确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // TODO: 处理消息发送失败
            }
        });
        return rabbitTemplate;
    }

    /**
     * 自定义消费者监听器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                             MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        // 设置并发消费者数量为1，确保消息顺序性
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }

    /**
     * 聊天消息交换机
     */
    @Bean
    public DirectExchange chatExchange() {
        return new DirectExchange("chat.direct");
    }

    /**
     * 聊天消息队列
     */
    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable(MqConstant.SEND_MSG_TOPIC)
                .build();
    }

    /**
     * 聊天消息绑定
     */
    @Bean
    public Binding chatBinding() {
        return BindingBuilder.bind(chatQueue())
                .to(chatExchange())
                .with(MqConstant.SEND_MSG_TOPIC);
    }

    /**
     * WebSocket推送交换机
     */
    @Bean
    public DirectExchange pushExchange() {
        return new DirectExchange("push.direct");
    }

    /**
     * WebSocket推送队列
     */
    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(MqConstant.PUSH_TOPIC)
                .build();
    }

    /**
     * WebSocket推送绑定
     */
    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue())
                .to(pushExchange())
                .with(MqConstant.PUSH_TOPIC);
    }

    /**
     * 用户登录消息交换机
     */
    @Bean
    public DirectExchange loginExchange() {
        return new DirectExchange("login.direct");
    }

    /**
     * 用户登录消息队列
     */
    @Bean
    public Queue loginQueue() {
        return QueueBuilder.durable(MqConstant.LOGIN_MSG_TOPIC)
                .build();
    }

    /**
     * 用户登录消息绑定
     */
    @Bean
    public Binding loginBinding() {
        return BindingBuilder.bind(loginQueue())
                .to(loginExchange())
                .with(MqConstant.LOGIN_MSG_TOPIC);
    }

    /**
     * 扫码消息交换机
     */
    @Bean
    public DirectExchange scanExchange() {
        return new DirectExchange("scan.direct");
    }

    /**
     * 扫码消息队列
     */
    @Bean
    public Queue scanQueue() {
        return QueueBuilder.durable(MqConstant.SCAN_MSG_TOPIC)
                .build();
    }

    /**
     * 扫码消息绑定
     */
    @Bean
    public Binding scanBinding() {
        return BindingBuilder.bind(scanQueue())
                .to(scanExchange())
                .with(MqConstant.SCAN_MSG_TOPIC);
    }
}