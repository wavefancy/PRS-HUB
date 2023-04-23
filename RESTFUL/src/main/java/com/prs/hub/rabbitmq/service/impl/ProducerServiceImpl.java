package com.prs.hub.rabbitmq.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.prs.hub.rabbitmq.dto.MQMessageDTO;
import com.prs.hub.rabbitmq.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author fanshupeng
 * @create 2023/4/3 16:24
 */
@Slf4j
@Service
public class ProducerServiceImpl implements ProducerService {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    //发送消息
    @Override
    public boolean sendTopicMessage(MQMessageDTO messageDTO,String exchangeName){
        log.info("消息内容："+ JSONObject.toJSONString(messageDTO));
        MessageProperties props = new MessageProperties();
        props.setMessageId(messageDTO.getMsgId()); // 设置消息 ID
        Message msg = new Message(messageDTO.getMessage().getBytes(), props);
        //指定 id
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(messageDTO.getMsgId());
        boolean flag = true;

        rabbitTemplate.convertAndSend(exchangeName,messageDTO.getRoutingKey(),msg,correlationData);

        log.info("发送消息flag:{}",flag);
        return flag;
    }
}
