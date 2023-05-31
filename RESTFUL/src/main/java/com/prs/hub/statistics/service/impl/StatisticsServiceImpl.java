package com.prs.hub.statistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.email.service.IMailService;
import com.prs.hub.practice.bo.MetadataEntryBo;
import com.prs.hub.practice.bo.RunnerDetailBo;
import com.prs.hub.practice.bo.RunnerDetailSpecialBo;
import com.prs.hub.practice.entity.MetadataEntry;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.rabbitmq.dto.MQMessageDTO;
import com.prs.hub.rabbitmq.service.ProducerService;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.statistics.service.StatisticsService;
import com.prs.hub.utils.CromwellUtil;
import com.prs.hub.utils.HttpClientUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fanshupeng
 * @create 2022/6/14 9:35
 */
@Slf4j
@Service
@EnableScheduling
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RunnerDetailBo runnerDetailBo;
    @Autowired
    private RunnerDetailSpecialBo runnerDetailSpecialBo;
    @Autowired
    private MetadataEntryBo metadataEntryBo;

    @Autowired
    private IMailService iMailService;
    /**
     * 访问地址
     */
    @Value("${system.path}")
    private String systemPath;

    private  String workflowsStatusUrl = "";
    private String workflowsQueryUrl = "";

    /**
     * 发送消息
     */
    @Autowired
    private ProducerService producerService;
    //PRS_HUB交换机名称
    private static final String PRS_HUB_TOPIC_EXCHANGE_NAME = "prs.hub.topic.exchange";
    @Value("${query.runner.detail.status.routing.key}")
    private String queryRunnerDetailStatusRoutingKey;

    /**
     * 查询runner数据
     * @return
     */
    @Override
    public List<RunnerStatisDTO> getRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO) {
        log.info("查询runner数据开始runnerStatisReqDTO"+JSON.toJSONString(runnerStatisReqDTO));

        RunnerDetail runnerDetail = new RunnerDetail();
        if(runnerStatisReqDTO.getUserId()!=null){
            runnerDetail.setUserId(runnerStatisReqDTO.getUserId());
        }
        if(StringUtils.isNotEmpty(runnerStatisReqDTO.getWorkflowExecutionUuid())){
            runnerDetail.setWorkflowExecutionUuid(runnerStatisReqDTO.getWorkflowExecutionUuid());
        }
        log.info("查询Runner表未结束的工作流入参runnerDetail="+ JSON.toJSONString(runnerDetail));
        List<RunnerStatisDTO>  runnerDetailList = runnerDetailSpecialBo.queryRunnerDetails(runnerDetail);
        log.info("查询Runner表未结束的工作流出参runnerDetailList="+ JSON.toJSONString(runnerDetailList));
        return runnerDetailList;
    }

    /**
     * 分页查询
     * @param runnerStatisReqDTO
     * @return
     */
    public IPage<RunnerStatisDTO> queryJobsPage(RunnerStatisReqDTO runnerStatisReqDTO) {
        log.info("分页查询runner数据开始runnerStatisReqDTO"+JSON.toJSONString(runnerStatisReqDTO));

        RunnerDetail runnerDetail = new RunnerDetail();
        if(runnerStatisReqDTO.getUserId()!=null){
            runnerDetail.setUserId(runnerStatisReqDTO.getUserId());
        }
        if(StringUtils.isNotEmpty(runnerStatisReqDTO.getWorkflowExecutionUuid())){
            runnerDetail.setWorkflowExecutionUuid(runnerStatisReqDTO.getWorkflowExecutionUuid());
        }
        log.info("分页查询Runner表未结束的工作流入参runnerDetail="+ JSON.toJSONString(runnerDetail));
        IPage<RunnerStatisDTO>  jobsPage = runnerDetailSpecialBo.queryJobsPage(runnerDetail,runnerStatisReqDTO.getSize(),runnerStatisReqDTO.getCurrent());
        log.info("分页查询Runner表未结束的工作流出参jobsPage="+ JSON.toJSONString(jobsPage));
        return jobsPage;
    }

    /**
     * 统计runner数据
     * @param runnerStatisReqDTO
     * @return
     */
    @Override
    public Long count(RunnerStatisReqDTO runnerStatisReqDTO){
        log.info("统计runner数据开始runnerStatisReqDTO"+JSON.toJSONString(runnerStatisReqDTO));

        QueryWrapper<RunnerDetail> runnerDetailQueryWrapper = new QueryWrapper<>();
        if(runnerStatisReqDTO != null){
            if(runnerStatisReqDTO.getStatus() != null){
                runnerDetailQueryWrapper.eq("status",runnerStatisReqDTO.getStatus());
            }
        }
        runnerDetailQueryWrapper.eq("is_delete",0);

        log.info("调用runnerDetailBo统计runner数据开始");
        Long countNum = runnerDetailBo.count(runnerDetailQueryWrapper);
        log.info("调用runnerDetailBo统计runner数据结束countNum="+countNum);
        return countNum;
    }

    /**
     * 定时任务：实时更新runner数据定
     */
    @Scheduled(cron = "0/10 * * * * *")
    private void realTimeUpdateRunnerDetail(){
        log.info("实时更新runner数据定时任务开始每10秒执行一次");

        QueryWrapper<RunnerDetail> RunnerDetailQueryWrapper = new QueryWrapper<>();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        statusList.add(1);
        RunnerDetailQueryWrapper.in("status",statusList);
        log.info("查询Runner表未结束的工作流入参RunnerDetailQueryWrapper="+ JSON.toJSONString(RunnerDetailQueryWrapper));
        List<RunnerDetail> runnerDetailList = runnerDetailBo.list(RunnerDetailQueryWrapper);
        log.info("查询Runner表未结束的工作流出参runnerDetailList="+ JSON.toJSONString(runnerDetailList));

        if(CollectionUtils.isNotEmpty(runnerDetailList)){
            StringBuffer weUuidStrBuffer = new StringBuffer();
            for (RunnerDetail runnerDetail:runnerDetailList) {
                String weUuid = runnerDetail.getWorkflowExecutionUuid();
                if(weUuidStrBuffer.length() == 0){
                    weUuidStrBuffer.append(weUuid);
                }else{
                    weUuidStrBuffer.append(","+weUuid);
                }
                //处理工作流的状态
//                changeRunnerDetailStatus(runnerDetail);
            }
            //发送消息查询工作流状态
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("uuidArr",weUuidStrBuffer.toString());
            this.sendQueryRunnerDetailStatusMessage(reqMap);
        }

    }

    private void changeRunnerDetailStatus(RunnerDetail runnerDetail) {
        String weUuid = runnerDetail.getWorkflowExecutionUuid();
        String statusStr = CromwellUtil.workflowsStatus(workflowsStatusUrl,weUuid);
        //运行状态 4:中止 3:完成，2:项目处于风险中，1:在进行中，0:未启动
        Integer status = 0;
        if("Succeeded".equals(statusStr)){
            status = 3;
        }else if("Failed".equals(statusStr)){
            status = 2;
        }else if("Running".equals(statusStr)){
            status = 1;
        }else if("Aborted".equals(statusStr)){
            status = 4;
        }
        if(status.intValue() != 0){
            //更新条件
            UpdateWrapper<RunnerDetail> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("workflow_execution_uuid",weUuid);

            //更新数据
            RunnerDetail runnerDetailReq = new RunnerDetail();

            //当结果为成功时获取结果地址
            if(status == 3){
                QueryWrapper<MetadataEntry> metadataEntryWrapper = new QueryWrapper<>();
                metadataEntryWrapper.eq("WORKFLOW_EXECUTION_UUID",weUuid);
                metadataEntryWrapper.eq("METADATA_KEY","outputs:out");
                log.info("查询工作流详情入参metadataEntryWrapper="+ JSON.toJSONString(metadataEntryWrapper));
                List<MetadataEntry> metadataEntryList = metadataEntryBo.selectList(metadataEntryWrapper);
                log.info("查询工作流详情出参metadataEntryList="+ JSON.toJSONString(metadataEntryList));
                //结果地址
                String resultPath = "";
                if(CollectionUtils.isNotEmpty(metadataEntryList)){
                    for (MetadataEntry metadataEntry:metadataEntryList) {
                        String metadataKey = metadataEntry.getMetadataKey();
                        if("outputs:out".equals(metadataKey) && StringUtils.isNotEmpty(metadataEntry.getMetadataValue()) && metadataEntry.getMetadataValue().endsWith(".tar.gz") ){
                            resultPath = metadataEntry.getMetadataValue();
                            break;
                        }
                    }

                }
                runnerDetailReq.setResultPath(resultPath);
            }

            if(status == 1){
                //查询工作流中正在执行的流程信息
                //设置Running状态的排序
                Map<String,String> map = new HashMap<String,String>();
                map.put("status","Running");

                log.info("调用workflowsQuery接口查询工作流中正在Running的流程信息");
                HashMap<String, Object> resMap = HttpClientUtil.get(map,workflowsQueryUrl);
                log.info("调用workflowsQuery接口查询工作流中正在Running的流程信息结束resMap="+JSON.toJSONString(resMap));

                if((Boolean) resMap.get("flag")){
                    JSONObject resultJson = (JSONObject) JSON.parse((String) resMap.get("result"));
                    JSONArray jsonArray = (JSONArray)resultJson.get("results");
                    //总running的条数
                    Integer totalResultsCount = (Integer) resultJson.get("totalResultsCount");
                    if(totalResultsCount > 0){
                        for(int i = 0 ; i < jsonArray.size() ; i++){
                            JSONObject runJB = (JSONObject)jsonArray.get(i);
                            String id = (String) runJB.get("id");
                            if(weUuid.equals(id)){
                                //设置Running状态的排序
                                runnerDetailReq.setQueue(totalResultsCount-i);
                                break;
                            }
                        }
                    }
                }
            }else{
                runnerDetailReq.setQueue(0);
            }

            runnerDetailReq.setStatus(status);
            log.info("更新runner数据入参runnerDetailReq="+JSON.toJSONString(runnerDetailReq));
            Boolean flag = runnerDetailBo.update(runnerDetailReq,updateWrapper);
            log.info("更新runner数据出参flag="+flag);
            if(status == 3){
                //发送验证邮件
                RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
                runnerStatisReqDTO.setWorkflowExecutionUuid(weUuid);
                RunnerStatisDTO runnerStatisDTO = this.getRunnerDetail(runnerStatisReqDTO).get(0);
                String content ="尊敬的用户,您好:<br>"
                        + runnerStatisDTO.getJobName()+"的"+runnerStatisDTO.getAlgorithmsName()
                        +"算法已经运行结束，,请点击下方的“登录”链接，跳转到PRS官网下载结果。<br><a href=\'"+systemPath+"\'>登录</a>"
                        + "<br>如非本人操作，请忽略该邮件。<br>(这是一封自动发送的邮件，请不要直接回复）";
                iMailService.sendResultMail(runnerStatisDTO.getEmail(),"运行结果下载提醒",content);
            }
        }
    }
    /**
     * 发送消息
     * @param reqMap
     */
    private Boolean sendQueryRunnerDetailStatusMessage(Map<String, Object> reqMap) {
        log.info("查询工作流状态变更 调用发送消息入参：{}", JSONObject.toJSONString(reqMap));

        String messageId = UUID.randomUUID().toString();

        JSONObject inputJson = new JSONObject(reqMap);

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(queryRunnerDetailStatusRoutingKey);
        messageDTO.setTag(queryRunnerDetailStatusRoutingKey);

        return producerService.sendTopicMessage(messageDTO,PRS_HUB_TOPIC_EXCHANGE_NAME);
    }
}
