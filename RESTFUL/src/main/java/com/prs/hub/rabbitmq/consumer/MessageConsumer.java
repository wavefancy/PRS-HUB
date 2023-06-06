package com.prs.hub.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.file.dto.PrsFileReqDTO;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.runnerdetail.service.RunnerDetailService;
import com.prs.hub.statistics.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    /**
     * 查询ld文件解析状态返回队列
     */
    private static final String QUERY_LD_FILE_STATUS_RES_QUEUE_NAME = "prs.hub.query.ld.file.status.res.queue";
    //发起ld文件解析返回队列名称
    public static final String UPLOAD_FILE_RES_QUEUE_NAME = "prs.hub.upload.file.res.queue";

    @Autowired
    private RunnerDetailService runnerDetailService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private FileService fileService;

    @RabbitListener(queues =CROMWELL_SUB_RES_QUEUE_NAME)
    public void cromwellSubResMsg(Message message){
        log.info("接受到队列 confirm.queue 消息:{}", JSONObject.toJSONString(message));

        String msg=new String(message.getBody());
        JSONObject msgJson = JSON.parseObject(msg);

        if(msgJson != null){
            String cromwellId = (String)msgJson.get("cromwellId");
            String userId = (String)msgJson.get("userId");
            String messageIdOld = (String)msgJson.get("messageIdOld");
            String resultPath = (String)msgJson.get("resultPath ");
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setWorkflowExecutionUuid(cromwellId);
            runnerStatisReqDTO.setMessageId(messageIdOld);
            runnerStatisReqDTO.setUserId(Long.valueOf(userId));
            runnerStatisReqDTO.setResultPath(resultPath);
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
        if(msgJson != null){
            for (Map.Entry entry : msgJson.entrySet()) {
                String key = (String) entry.getKey();
                JSONObject resJson = (JSONObject)entry.getValue();
                String status = (String)resJson.get("status");
                Boolean flag = statisticsService.changeRunnerDetailStatusByUuid(key,status);
            }
        }

        log.info("监听查询job状态结果消息队列:{}",msg);
    }

    /**
     * 发起ld文件解析返回队列结果
     * @param message
     */
    @RabbitListener(queues =UPLOAD_FILE_RES_QUEUE_NAME)
    public void uploadFileResMsg(Message message){
        log.info("接受到发起ld文件解析返回结果消息:{}", JSONObject.toJSONString(message));

        String msg=new String(message.getBody());
        JSONObject msgJson = JSON.parseObject(msg);
        if(msgJson != null){
            String cromwellId = (String)msgJson.get("cromwellId");
            String fileId = (String)msgJson.get("fileId");

            PrsFileReqDTO prsFileReqDTO = new PrsFileReqDTO();
            prsFileReqDTO.setId(Long.valueOf(fileId));
            prsFileReqDTO.setParsingId(cromwellId);
            try {
                log.info("保存ld文件解析id入参prsFileReqDTO：{}",JSONObject.toJSONString(prsFileReqDTO));
                Boolean flag = fileService.updateFile(prsFileReqDTO);
                log.info("保存ld文件解析id出参flag：{}",flag);
            }catch (Exception e){
                log.error("保存ld文件解析id失败:{}",e.getMessage());
            }
        }
        log.info("接受到发起ld文件解析返回结果消息:{}",msg);
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
    /**
     * ld文件解析状态返回结果
     * @param message
     */
    @RabbitListener(queues =QUERY_LD_FILE_STATUS_RES_QUEUE_NAME)
    public void queryLdFileStatusResMsg(Message message){
        log.info("ld文件解析状态返回结果消息:{}", JSONObject.toJSONString(message));

        String msg=new String(message.getBody());
        JSONObject msgJson = JSON.parseObject(msg);
        if(msgJson != null){
            try {
                for (Map.Entry entry : msgJson.entrySet()) {
                    String key = (String) entry.getKey();
                    String statusStr = (String)entry.getValue();
                    PrsFile prsFile = new PrsFile();
                    prsFile.setParsingId(key);

                    //根据解析id查询文件信息
                    List<PrsFile> prsFileList = fileService.getFileList(prsFile);
                    if(CollectionUtils.isNotEmpty(prsFileList)){
                        PrsFile prsFileRes = prsFileList.get(0);
                        //修改文件解析状态
                        PrsFileReqDTO prsFileReqDTO = new PrsFileReqDTO();
                        prsFileReqDTO.setId(prsFileRes.getId());
                        prsFileReqDTO.setParsingStatus("Succeeded".equals(statusStr)? "Y" : "N");
                        fileService.updateFile(prsFileReqDTO);
                    }
                }
            }catch (Exception e){
                log.error("ld文件解析状态返回结果消息处理异常:{}",e.getMessage());
            }
        }
    }

}
