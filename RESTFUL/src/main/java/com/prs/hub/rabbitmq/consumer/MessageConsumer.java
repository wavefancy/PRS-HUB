package com.prs.hub.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.runnerdetail.service.RunnerDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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

    //查询job状态结果
    public static final String QUERY_RUNNER_DETAIL_STATUS_RES_QUEUE_NAME = "prs.hub.query.runner.detail.status.res.queue";

    @Autowired
    private RunnerDetailService runnerDetailService;

    @RabbitListener(queues =CROMWELL_SUB_RES_QUEUE_NAME)
    public void cromwellSubResMsg(Message message){
        log.info("接受到队列 confirm.queue 消息:{}", JSONObject.toJSONString(message));

        String msg=new String(message.getBody());
        JSONObject msgJson = JSON.parseObject(msg);

        if(msgJson != null){
            String cromwellId = (String)msgJson.get("cromwellId");
            String userId = (String)msgJson.get("userId");
            String messageIdOld = (String)msgJson.get("messageIdOld");
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setWorkflowExecutionUuid(cromwellId);
            runnerStatisReqDTO.setMessageId(messageIdOld);
            runnerStatisReqDTO.setUserId(Long.valueOf(userId));
            runnerDetailService.updateRunnerDetail(runnerStatisReqDTO);
        }

        log.info("接受到队列 confirm.queue 消息:{}",msg);
    }

    /**
     * 监听查询job状态结果消息队列
     * @param message
     */
    @RabbitListener(queues =QUERY_RUNNER_DETAIL_STATUS_RES_QUEUE_NAME)
    public void queryJobsStatusResmsg(Message message){
        log.info("监听查询job状态结果消息队列:{}", JSONObject.toJSONString(message));

        String msg = new String(message.getBody());
        JSONObject msgJson = JSON.parseObject(msg);
        if(msgJson == null){

        }

        log.info("监听查询job状态结果消息队列:{}",msg);
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
