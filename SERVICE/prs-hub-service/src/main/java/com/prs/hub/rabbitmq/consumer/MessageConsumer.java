package com.prs.hub.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prs.hub.algorithms.dto.AlgorithmsParameterDTO;
import com.prs.hub.algorithms.service.AlgorithmsParameterService;
import com.prs.hub.files.dto.FileParsingDTO;
import com.prs.hub.files.service.FileService;
import com.prs.hub.rabbitmq.dto.MQMessageDTO;
import com.prs.hub.rabbitmq.service.ProducerService;
import com.prs.hub.utils.CromwellUtil;
import com.prs.hub.utils.ProcessUtils;
import com.prs.hub.utils.StringUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author fanshupeng
 * @create 2023/4/10 17:08
 */

@Component
@Slf4j
public class MessageConsumer {
    //交换机名称
    @Value("${cromwell.exchange}")
    private String CROMWELL_EXCHANGE;
    //算法参数队列名称
    public static final String ALGORITHMS_PARAMETER_QUEUE_NAME = "prs.hub.algorithms.parameter.queue";

    //Upload交换机名称
    private static final String PRS_HUB_UPLOAD_EXCHANGE_NAME = "prs.hub.upload.exchange";

    //Upload文件队列名称
    public static final String UPLOAD_QUEUE_NAME = "prs.hub.upload.queue";

    //查询LD文件解析状态队列名称
    public static final String QUERY_FILE_STATUS_QUEUE_NAME = "prs.hub.query.file.status.queue";

    @Autowired
    private AlgorithmsParameterService algorithmsParameterService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProcessUtils processUtils;

    @Value("${cromwell.sub.res.routing.key}")
    private String cromwellSubResRoutingKey;

    @Value("${upload.file.sync.res.routing.key}")
    private String fileSyncResRoutingKey;

    @Value("${cromwell.workflows.status.url}")
    private  String workflowsStatusUrl;

    @RabbitListener(queues =ALGORITHMS_PARAMETER_QUEUE_NAME)
    public void algorithmsParameterMsg(Message message, Channel channel) throws IOException {
        log.info("接受到队列 confirm.queue 消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);

        //提交工作流
        AlgorithmsParameterDTO algorithmsParameterDTO = toAlgorithmsParameterDTO(msgJson);
        try {
            String cromwellId = algorithmsParameterService.submitWorkflow(algorithmsParameterDTO);

            //向前端发送工作流id消息
            Map<String,String> resMsg = new HashMap<>();
            resMsg.put("cromwellId",cromwellId);
            resMsg.put("userId",algorithmsParameterDTO.getUserId());
            resMsg.put("messageIdOld",messageProperties.getMessageId());
            String messageId = UUID.randomUUID().toString();
            //当前系统时间
            LocalDateTime now = LocalDateTime.now();

            MQMessageDTO messageDTO = new MQMessageDTO();
            messageDTO.setMessage(resMsg.toString());
            messageDTO.setMsgId(messageId);
            messageDTO.setRoutingKey(cromwellSubResRoutingKey);
            messageDTO.setExchange(CROMWELL_EXCHANGE);
            messageDTO.setTag("prs.hub");

            producerService.sendCromwellMessage(messageDTO);

            // 手动确认algorithmsParameter消息已经被消费
            channel.basicAck(messageProperties.getDeliveryTag(), false);

            log.info("已提交cromwell 向prs-hub发送消息:{}",JSONObject.toJSONString(messageDTO));

        }catch (Exception e){
            log.error("提交cromwell出现异常:{}",e.getMessage());
            // 处理消息异常，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);

        }

    }

    /**
     * 处理文件上传消息
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =UPLOAD_QUEUE_NAME)
    public void uploadFileMsg(Message message, Channel channel) throws IOException {
        log.info("接受到文件上传消息:{}", JSONObject.toJSONString(message));

        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);
        String fileId = (String)msgJson.get("fileId");
        String fileType = (String)msgJson.get("fileType");
        String filePath = (String)msgJson.get("filePath");
        String fileName = (String)msgJson.get("fileName");
        String suffixName = (String)msgJson.get("suffixName");
        String userId = (String)msgJson.get("userId");
        String pop = (String)msgJson.get("pop");
        try {
            //复制文件

            //拼装源文件地址
            String sourcePath = filePath+fileName+suffixName;
            //目标文件名
            String destinationFileName = fileName + "_" + userId + "_" + messageProperties.getMessageId() + suffixName;
            boolean rsyncFlag = processUtils.rsyncPull(sourcePath,destinationFileName);

            Map<String,Object> resMsg = new HashMap<>();
            resMsg.put("rsyncFlag",rsyncFlag);
            if (rsyncFlag){
                if("LD".equals(fileType)){

                    FileParsingDTO fileParsingDTO = new FileParsingDTO();
                    fileParsingDTO.setDestinationFileName(destinationFileName);
                    fileParsingDTO.setPopValue(pop);
                    fileParsingDTO.setUserId(userId);
                    //调用解析LD文件工作流
                    String cromwellId = fileService.fileParsing(fileParsingDTO);
                    resMsg.put("cromwellId",cromwellId);
                }

                //发送文件同步完成消息
                resMsg.put("fileId",fileId);
                String messageId = UUID.randomUUID().toString();
                MQMessageDTO messageDTO = new MQMessageDTO();
                messageDTO.setMessage(resMsg.toString());
                messageDTO.setMsgId(messageId);
                messageDTO.setRoutingKey(fileSyncResRoutingKey);
                messageDTO.setExchange(PRS_HUB_UPLOAD_EXCHANGE_NAME);
                messageDTO.setTag("prs.hub");

                producerService.sendCromwellMessage(messageDTO);
                // 手动确认algorithmsParameter消息已经被消费
                channel.basicAck(messageProperties.getDeliveryTag(), false);
            }else {
                // 同步文件出现问题，将消息重新发送到死信队列中
                channel.basicNack(messageProperties.getDeliveryTag(), false, false);
            }

        }catch (Exception e){
            log.error("同步文件异常：{}",e.getMessage());
            // 同步文件出现问题，将消息重新发送到死信队列中
            channel.basicNack(messageProperties.getDeliveryTag(), false, false);
        }
    }

    /**
     * 监听查询LD文件解析状态
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues =QUERY_FILE_STATUS_QUEUE_NAME)
    public void queryFileStatusMsg(Message message, Channel channel) throws IOException {
        log.info("监听查询LD文件解析状态消息:{}", JSONObject.toJSONString(message));


        MessageProperties messageProperties = message.getMessageProperties();
        String msg=new String(message.getBody());
        JSONObject msgJson= JSON.parseObject(msg);
        String[] uuidArr = ((String)msgJson.get("uuidArr")).split(",");
        log.info("uuidArr:{}",uuidArr);

        queryFileStatus(uuidArr);

    }

    private void queryFileStatus(String[] uuidArr) {
        for (String uuid : uuidArr) {
                String statusStr = CromwellUtil.workflowsStatus(workflowsStatusUrl,uuid);
                if("Succeeded".equals(statusStr)){

                }
        }
    }

    /**
     * 转换消息体
     * @param msgJson
     * @return
     */
    private AlgorithmsParameterDTO toAlgorithmsParameterDTO(JSONObject msgJson) {
        log.info("转换消息体开始msgJson="+msgJson.toJSONString());

        AlgorithmsParameterDTO algorithmsParameterDTO = new AlgorithmsParameterDTO();

        /**
         * 用户id
         */
        String userId = (String) msgJson.get("userId");
        if (StringUtils.isNotEmpty(userId)){
            algorithmsParameterDTO.setUserId(userId);
        }
        /**
         * 执行wdl脚本的地址
         */
        String wdlPath = (String) msgJson.get("wdlPath");
        if (StringUtils.isNotEmpty(wdlPath)){
            algorithmsParameterDTO.setWdlPath(wdlPath);
        }
        /**
         * 算法id
         */
        String algorithmsId = msgJson.get("algorithmsId").toString();
        if (StringUtils.isNotEmpty(algorithmsId)){
            algorithmsParameterDTO.setAlgorithmsId(algorithmsId);
        }
        /**
         * 用户填写的参数数据
         */
        JSONObject inputJson = (JSONObject) msgJson.get("inputJson");
        if (inputJson != null){
            algorithmsParameterDTO.setInputJson(inputJson);
        }

        log.info("转换消息体结束algorithmsParameterDTO="+JSONObject.toJSONString(algorithmsParameterDTO));
        return algorithmsParameterDTO;
    }
}
