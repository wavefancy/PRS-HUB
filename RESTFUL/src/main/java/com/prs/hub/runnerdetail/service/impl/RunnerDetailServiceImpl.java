package com.prs.hub.runnerdetail.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.practice.bo.RunnerDetailBo;
import com.prs.hub.practice.bo.RunnerDetailSpecialBo;
import com.prs.hub.practice.entity.MetadataEntry;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.runnerdetail.service.RunnerDetailService;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/7/20 16:54
 */
@Slf4j
@Service
public class RunnerDetailServiceImpl implements RunnerDetailService {
    @Autowired
    private RunnerDetailBo runnerDetailBo;
    @Autowired
    private RunnerDetailSpecialBo runnerDetailSpecialBo;

    /**
     * 查询数据
     * @param runnerStatisReqDTO
     * @return
     */
    @Override
    public RunnerDetail selectRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO){
        log.info("查询数据runnerStatisReqDTO="+ JSON.toJSONString(runnerStatisReqDTO));
        Boolean flag = false;
        RunnerDetail runnerDetail =null;
        QueryWrapper<RunnerDetail> queryWrapper = new QueryWrapper<RunnerDetail>();
        if(runnerStatisReqDTO.getId() != null){
            queryWrapper.eq("id",runnerStatisReqDTO.getId());
        }
        if(StringUtils.isNotEmpty(runnerStatisReqDTO.getWorkflowExecutionUuid() )){
            queryWrapper.eq("workflow_execution_uuid",runnerStatisReqDTO.getWorkflowExecutionUuid());
        }
        try {
            runnerDetail = runnerDetailBo.getOne(queryWrapper);
        }catch (Exception e){
            log.error("查询RunnerDetail数据异常",e);
        }
        log.info("查询RunnerDetail数据runnerDetail="+JSON.toJSONString(runnerDetail));
        return runnerDetail;
    }

    /**
     * 删除数据
     * @param runnerStatisReqDTO
     * @return
     */
    @Override
    public boolean deleteRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO){
        log.info("删除数据runnerStatisReqDTO="+ JSON.toJSONString(runnerStatisReqDTO));
        Boolean flag = false;
        QueryWrapper<RunnerDetail> queryWrapper = new QueryWrapper<>();
        if(runnerStatisReqDTO.getId() != null){
            queryWrapper.eq("id",runnerStatisReqDTO.getId());
        }
        if(StringUtils.isNotEmpty(runnerStatisReqDTO.getWorkflowExecutionUuid() )){
            queryWrapper.eq("workflow_execution_uuid",runnerStatisReqDTO.getWorkflowExecutionUuid());
        }
        try {
            flag = runnerDetailBo.remove(queryWrapper);
        }catch (Exception e){
            log.error("删除数据异常",e);
        }
        log.info("删除数据flag="+flag);
        return flag;
    }

    /**
     * 修改数据
     * @param runnerStatisReqDTO
     * @return
     */
    @Override
    public boolean updateRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO){
        log.info("更新runner数据runnerStatisReqDTO="+ JSON.toJSONString(runnerStatisReqDTO));
        Boolean flag = false;

        try {
            //更新条件
            UpdateWrapper<RunnerDetail> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("workflow_execution_uuid",runnerStatisReqDTO.getWorkflowExecutionUuid());

            //更新数据
            RunnerDetail runnerDetailReq = new RunnerDetail();

            runnerDetailReq.setStatus(runnerStatisReqDTO.getStatus());
            log.info("更新runner数据入参runnerDetailReq="+JSON.toJSONString(runnerDetailReq));
            flag = runnerDetailBo.update(runnerDetailReq,updateWrapper);
            log.info("更新runner数据出参flag="+flag);
        }catch (Exception e){
            log.error("更新runner数据异常",e);
        }
        log.info("更新runner数据flag="+flag);
        return flag;
    }

}
