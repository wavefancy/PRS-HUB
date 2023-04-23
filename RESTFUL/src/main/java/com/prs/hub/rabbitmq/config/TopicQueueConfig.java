package com.prs.hub.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * topic消息队列配置文件
 * @author fanshupeng
 * @create 2023/4/7 16:58
 */


@Configuration
public class TopicQueueConfig {
    //cromwell交换机名称
    private static final String CROMWELL_EXCHANGE_NAME = "prs.hub.cromwell.topic.exchange";
    //cromwell队列名称
    public static final String CROMWELL_SUB_RES_QUEUE_NAME = "prs.hub.cromwell.sub.res.queue";

    //声明cromwell topic Exchange
    @Bean("cromwellTopicExchange")
    public TopicExchange cromwellTopicExchange(){
        return (TopicExchange) ExchangeBuilder.topicExchange(CROMWELL_EXCHANGE_NAME)
                .durable(true)
                .build();
    }
    // 声明cromwell topic队列
    @Bean("cromwellTopicQueue")
    public Queue cromwellTopicQueue(){
        return QueueBuilder.durable(CROMWELL_SUB_RES_QUEUE_NAME).build();
    }
    // 声明cromwell topic队列绑定关系
    @Bean("cromwellQueueBinding")
    public Binding cromwellQueueBinding(@Qualifier("cromwellTopicQueue") Queue queue,
                                @Qualifier("cromwellTopicExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("prs.hub.cromwell.#");
    }
}
