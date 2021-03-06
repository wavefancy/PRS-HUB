package com.prs.hub.statistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.email.service.IMailService;
import com.prs.hub.practice.bo.MetadataEntryBo;
import com.prs.hub.practice.bo.RunnerDetailBo;
import com.prs.hub.practice.bo.RunnerDetailSpecialBo;
import com.prs.hub.practice.entity.MetadataEntry;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.statistics.dto.RunnerStatisDTO;
import com.prs.hub.statistics.dto.RunnerStatisReqDTO;
import com.prs.hub.statistics.service.StatisticsService;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    IMailService iMailService;
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
        List<RunnerStatisDTO> runnerDetailList = runnerDetailSpecialBo.queryRunnerDetails(runnerDetail);
        log.info("查询Runner表未结束的工作流出参runnerDetailList="+ JSON.toJSONString(runnerDetailList));
        return runnerDetailList;
    }

    /**
     * 定时任务：实时更新runner数据定
     */
    @Scheduled(cron = "30 * * * * ?")
    private void realTimeUpdateRunnerDetail(){
        log.info("实时更新runner数据定时任务开始每30秒执行一次");

        QueryWrapper<RunnerDetail> RunnerDetailQueryWrapper = new QueryWrapper<>();
        List<Integer> statusList = new ArrayList<>();
        statusList.add(0);
        statusList.add(1);
        RunnerDetailQueryWrapper.in("status",statusList);
 
        log.info("查询Runner表未结束的工作流入参RunnerDetailQueryWrapper="+ JSON.toJSONString(RunnerDetailQueryWrapper));
        List<RunnerDetail> runnerDetailList = runnerDetailBo.list(RunnerDetailQueryWrapper);
        log.info("查询Runner表未结束的工作流出参runnerDetailList="+ JSON.toJSONString(runnerDetailList));

        if(CollectionUtils.isNotEmpty(runnerDetailList)){
            for (RunnerDetail runnerDetail:runnerDetailList) {

                String weUuid = runnerDetail.getWorkflowExecutionUuid();
                QueryWrapper<MetadataEntry> metadataEntryWrapper = new QueryWrapper<>();
                metadataEntryWrapper.eq("WORKFLOW_EXECUTION_UUID",weUuid);

                log.info("查询工作流详情入参metadataEntryWrapper="+ JSON.toJSONString(metadataEntryWrapper));
                List<MetadataEntry> metadataEntryList = metadataEntryBo.selectList(metadataEntryWrapper);
                log.info("查询工作流详情出参metadataEntryList="+ JSON.toJSONString(metadataEntryList));

                if(CollectionUtils.isNotEmpty(metadataEntryList)){
                    String statusStr = "";
                    String resultPath = "";
                    for (MetadataEntry metadataEntry:metadataEntryList) {
                        String metadataKey = metadataEntry.getMetadataKey();
                        if("status".equals(metadataKey)){
                            statusStr = statusStr+"#"+metadataEntry.getMetadataValue();
                        }
                        if("outputs:out".equals(metadataKey)){
                            resultPath = metadataEntry.getMetadataValue();
                        }
                    }
                    //运行状态 3:完成，2:项目处于风险中，1:在进行中，0:未启动
                    Integer status = 0;
                    if(statusStr.indexOf("Succeeded") != -1){
                        status = 3;
                    }else if(statusStr.indexOf("Failed") != -1){
                        status = 2;
                    }else if(statusStr.indexOf("Running") != -1){
                        status = 1;
                    }
                    if(status.intValue() != runnerDetail.getStatus()){
                        //更新条件
                        UpdateWrapper<RunnerDetail> updateWrapper = new UpdateWrapper();
                        updateWrapper.eq("workflow_execution_uuid",weUuid);

                        //更新数据
                        RunnerDetail runnerDetailReq = new RunnerDetail();
                        runnerDetailReq.setResultPath(resultPath);
                        runnerDetailReq.setStatus(status);
                        log.info("更新runner数据入参runnerDetailReq="+JSON.toJSONString(runnerDetailReq));
                        Boolean flag = runnerDetailBo.update(runnerDetailReq,updateWrapper);
                        log.info("更新runner数据出参flag="+flag);
                        if(status == 3){
                            //发送验证邮件
                            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
                            runnerStatisReqDTO.setWorkflowExecutionUuid(weUuid);
                            RunnerStatisDTO runnerStatisDTO = this.getRunnerDetail(runnerStatisReqDTO).get(0);
                            String content = runnerStatisDTO.getJobName()+"的"+runnerStatisDTO.getAlgorithmsName()+"算法已经运行结束，";
                            iMailService.sendResultMail(runnerStatisDTO.getEmail(),"运行结果下载提醒",content);
                        }
                    }
                }
            }
        }

    }
}
