package com.prs.hub.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author fanshupeng
 * @create 2023/4/10 17:08
 */

@Component
@Slf4j
public class MessageConsumer {
    //队列名称
    public static final String CROMWELL_SUB_RES_QUEUE_NAME = "prs.hub.cromwell.sub.res.queue";

    //上传文件同步结果队列名称
    public static final String FILE_SYNC_RES_QUEUE_NAME = "prs.hub.upload.file.sync.res.queue";

    @RabbitListener(queues =CROMWELL_SUB_RES_QUEUE_NAME)
    public void cromwellSubResMsg(Message message){
        log.info("接受到队列 confirm.queue 消息:{}", JSONObject.toJSONString(message));

        String msg=new String(message.getBody());


        log.info("接受到队列 confirm.queue 消息:{}",msg);
    }

    /**
     * 上传文件同步结果
     * @param message
     */
    @RabbitListener(queues =FILE_SYNC_RES_QUEUE_NAME)
    public void uploadFileSyncResMsg(Message message){
        log.info("接受到上传文件同步结果消息:{}", JSONObject.toJSONString(message));

        String msg=new String(message.getBody());


        log.info("接受到上传文件同步结果消息:{}",msg);
    }

}
