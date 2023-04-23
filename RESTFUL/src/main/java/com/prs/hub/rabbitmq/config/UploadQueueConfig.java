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

    //上传文件同步结果队列名称
    public static final String FILE_SYNC_RES_QUEUE_NAME = "prs.hub.upload.file.sync.res.queue";
    //上传文件同步结果路由名称
    public static final String FILE_SYNC_RESROUTING_NAME = "prs.hub.upload.file.sync.res";

    //声明Upload Exchange
    @Bean("uploadExchange")
    public TopicExchange uploadExchange(){
        return (TopicExchange) ExchangeBuilder.topicExchange(PRS_HUB_UPLOAD_EXCHANGE_NAME)
                .durable(true)
                .build();
    }
    // 声明Upload队列
    @Bean("fileSyncResQueue")
    public Queue fileSyncResQueue(){
        return QueueBuilder.durable(FILE_SYNC_RES_QUEUE_NAME).build();
    }

    // 声明Upload队列绑定关系
    @Bean("fileSyncResQueueBinding")
    public Binding fileSyncResQueueBinding(@Qualifier("fileSyncResQueue") Queue queue,
                                @Qualifier("uploadExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(FILE_SYNC_RESROUTING_NAME);
    }
}
