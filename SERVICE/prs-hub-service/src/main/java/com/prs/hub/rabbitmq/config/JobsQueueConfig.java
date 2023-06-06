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
public class JobsQueueConfig {
    //PRS_HUB交换机名称
    private static final String PRS_HUB_TOPIC_EXCHANGE_NAME = "prs.hub.topic.exchange";
    //PRS_HUB死信交换机名称
    private static final String PRS_HUB_EXCHANGE_DLX_NAME = "prs.hub.exchange.dlx";

    //PRS_HUB队列名称
    public static final String ALGORITHMS_PARAMETER_QUEUE_NAME = "prs.hub.algorithms.parameter.queue";
    //PRS_HUB队列名称
    public static final String QUERY_RUNNER_DETAIL_STATUS_QUEUE_NAME = "prs.hub.query.runner.detail.status.queue";
    //PRS_HUB死信队列名称
    public static final String DEAD_LETTER_QUEUE_QUEUE_NAME = "prs.hub.dead_letter_queue";

    //声明PRS_HUBtopic Exchange
    @Bean("prsHubTopicExchange")
    public TopicExchange prsHubTopicExchange(){
        return (TopicExchange) ExchangeBuilder.topicExchange(PRS_HUB_TOPIC_EXCHANGE_NAME)
                .durable(true)
                .build();
    }
    // 声明ALGORITHMS_PARAMETER队列
    @Bean("algorithmsParameterQueue")
    public Queue algorithmsParameterQueue(){
        return QueueBuilder.durable(ALGORITHMS_PARAMETER_QUEUE_NAME).build();
    }
    // 声明ALGORITHMS_PARAMETER topic队列绑定关系
    @Bean("queueBinding")
    public Binding queueBinding(@Qualifier("algorithmsParameterQueue") Queue queue,
                                @Qualifier("prsHubTopicExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("prs.hub.algorithms.#");
    }
    // 声明queryRunnerDetailStatus队列
    @Bean("queryRunnerDetailStatusQueue")
    public Queue queryRunnerDetailStatusQueue(){
        return QueueBuilder.durable(QUERY_RUNNER_DETAIL_STATUS_QUEUE_NAME).build();
    }
    // 声明queryRunnerDetailStatus topic队列绑定关系
    @Bean("queryRunnerDetailStatusQueueBinding")
    public Binding queryRunnerDetailStatusQueueBinding(@Qualifier("queryRunnerDetailStatusQueue") Queue queue,
                                @Qualifier("prsHubTopicExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("prs.hub.query.runner.detail.status");
    }
}
