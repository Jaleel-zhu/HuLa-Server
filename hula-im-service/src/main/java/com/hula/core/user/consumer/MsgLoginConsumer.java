package com.hula.core.user.consumer;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.LoginMessageDTO;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 在本地服务上找寻对应channel，将对应用户登陆，并触发所有用户收到上线事件
 * @author nyh
 */
@Component
public class MsgLoginConsumer {
    @Resource
    private WebSocketService webSocketService;

    @RabbitListener(queues = MqConstant.LOGIN_MSG_TOPIC)
    public void onMessage(LoginMessageDTO loginMessageDTO) {
        // 尝试登录
        webSocketService.scanLoginSuccess(loginMessageDTO);
    }
}
