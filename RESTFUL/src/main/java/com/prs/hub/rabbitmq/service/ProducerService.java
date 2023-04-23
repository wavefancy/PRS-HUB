package com.prs.hub.rabbitmq.service;

import com.prs.hub.rabbitmq.dto.MQMessageDTO;

/**
 * @author fanshupeng
 * @create 2023/4/3 16:23
 */
public interface ProducerService {
    /**
     * 发送消息
     * @param messageDTO
     * @return
     */
    boolean sendTopicMessage(MQMessageDTO messageDTO,String exchangeName);
}
