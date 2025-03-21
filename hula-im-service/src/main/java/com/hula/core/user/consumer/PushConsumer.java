package com.hula.core.user.consumer;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.PushMessageDTO;
import com.hula.core.user.domain.enums.WSPushTypeEnum;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


/**
 * @author nyh
 */
@Slf4j
@Component
public class PushConsumer {
    @Resource
    private WebSocketService webSocketService;

    @RabbitListener(queues = MqConstant.PUSH_TOPIC)
    public void onMessage(PushMessageDTO message) {
		log.error("收到消息，推送到前端......", message);
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());
        switch (wsPushTypeEnum) {
            case WSPushTypeEnum.USER:
                message.getUidList().forEach(uid -> {
                    webSocketService.sendUser(message.getWsBaseMsg(), uid);
                });
                break;
            case WSPushTypeEnum.ALL:
                webSocketService.sendAll(message.getWsBaseMsg(), null);
                break;
        }
    }
}
