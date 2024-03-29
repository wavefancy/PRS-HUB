package com.prs.hub.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fanshupeng
 * @create 2023/4/14 16:46
 */

@Configuration
public class UploadQueueConfig {
    //Upload交换机名称
    private static final String PRS_HUB_UPLOAD_EXCHANGE_NAME = "prs.hub.upload.exchange";

    //Upload队列名称
    public static final String UPLOAD_QUEUE_NAME = "prs.hub.upload.queue";
    //查询LD文件解析状态队列名称
    public static final String QUERY_FILE_STATUS_QUEUE_NAME = "prs.hub.query.file.status.queue";
    //删除文件队列名称
    public static final String DELETE_FILE_QUEUE_NAME = "prs.hub.delete.file.queue";
    public static final String DELETE_FILE_ROUTING_KEY_NAME = "prs.hub.delete.file";

    //声明Upload Exchange
    @Bean("uploadExchange")
    public TopicExchange uploadExchange(){
        return (TopicExchange) ExchangeBuilder.topicExchange(PRS_HUB_UPLOAD_EXCHANGE_NAME)
                .durable(true)
                .build();
    }
    // 声明Upload队列
    @Bean("uploadQueue")
    public Queue uploadQueue(){
        return QueueBuilder.durable(UPLOAD_QUEUE_NAME).build();
    }
    // 声明查询LD文件解析状态队列
    @Bean("queryFileStatusQueue")
    public Queue queryFileStatusQueue(){
        return QueueBuilder.durable(QUERY_FILE_STATUS_QUEUE_NAME).build();
    }
    // 声明删除文件队列
    @Bean("deleteFileQueue")
    public Queue deleteFileQueue(){
        return QueueBuilder.durable(DELETE_FILE_QUEUE_NAME).build();
    }

    // 声明Upload队列绑定关系
    @Bean("uploadQueueBinding")
    public Binding uploadQueueBinding(@Qualifier("uploadQueue") Queue queue,
                                @Qualifier("uploadExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("prs.hub.upload.file");
    }
    // 声明Upload队列绑定关系
    @Bean("queryFileStatusQueueBinding")
    public Binding queryFileStatusQueueBinding(@Qualifier("queryFileStatusQueue") Queue queue,
                                @Qualifier("uploadExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("prs.hub.query.file.status.#");
    }
    // 声明删除文件绑定关系
    @Bean("deleteFileQueueBinding")
    public Binding deleteFileQueueBinding(@Qualifier("deleteFileQueue") Queue queue,
                                @Qualifier("uploadExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DELETE_FILE_ROUTING_KEY_NAME);
    }
}
