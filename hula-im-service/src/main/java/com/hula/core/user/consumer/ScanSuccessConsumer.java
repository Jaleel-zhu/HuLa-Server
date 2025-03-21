package com.hula.core.user.consumer;

import com.hula.common.constant.MqConstant;
import com.hula.common.domain.dto.ScanSuccessMessageDTO;
import com.hula.core.user.service.WebSocketService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 将扫码成功的信息发送给对应的用户,等待授权
 * @author nyh
 */
@Component
public class ScanSuccessConsumer {
    @Resource
    private WebSocketService webSocketService;

    @RabbitListener(queues = MqConstant.SCAN_MSG_TOPIC)
    public void onMessage(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        webSocketService.scanSuccess(scanSuccessMessageDTO);
    }
}
