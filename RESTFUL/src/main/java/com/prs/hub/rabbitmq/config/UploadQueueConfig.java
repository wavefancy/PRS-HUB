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

    //发起ld文件解析返回队列名称
    public static final String UPLOAD_FILE_RES_QUEUE_NAME = "prs.hub.upload.file.res.queue";
    //发起ld文件解析返回路由
    private static final String UPLOAD_FILE_RES_ROUTING_KEY_NAME = "prs.hub.upload.file.res";

    /**
     * 查询ld文件解析状态返回队列
     */
    private static final String QUERY_LD_FILE_STATUS_RES_QUEUE_NAME = "prs.hub.query.ld.file.status.res.queue";
    /**
     * 查询ld文件解析状态返回队列路由
     */
    private static final String QUERY_LD_FILE_STATUS_RES_ROUTING_KEY_NAME = "prs.hub.query.ld.file.status.res";

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
    // 声明查询ld文件解析状态返回队列
    @Bean("queryLdFileStatusResQueue")
    public Queue queryLdFileStatusResQueue(){
        return QueueBuilder.durable(QUERY_LD_FILE_STATUS_RES_QUEUE_NAME).build();
    }

    // 声明查询ld文件解析状态返回队列绑定关系
    @Bean("queryLdFileStatusResQueueBinding")
    public Binding queryLdFileStatusResQueueBinding(@Qualifier("queryLdFileStatusResQueue") Queue queue,
                                @Qualifier("uploadExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(QUERY_LD_FILE_STATUS_RES_ROUTING_KEY_NAME);
    }
    // 声明发起ld文件解析返回队列
    @Bean("uploadFileResQueue")
    public Queue uploadFileResQueue(){
        return QueueBuilder.durable(UPLOAD_FILE_RES_QUEUE_NAME).build();
    }

    // 声明发起ld文件解析返回队列绑定关系
    @Bean("uploadFileResQueueBinding")
    public Binding uploadFileResQueueBinding(@Qualifier("uploadFileResQueue") Queue queue,
                                @Qualifier("uploadExchange") TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(UPLOAD_FILE_RES_ROUTING_KEY_NAME);
    }
}
