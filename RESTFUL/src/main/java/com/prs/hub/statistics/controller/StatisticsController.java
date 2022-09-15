package com.prs.hub.statistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;
import com.prs.hub.algorithms.service.ParameterEnterService;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.runnerdetail.dto.RunnerStatisResDTO;
import com.prs.hub.runnerdetail.service.RunnerDetailService;
import com.prs.hub.runnerdetailtofile.dto.RunnerDetailToFileReqDTO;
import com.prs.hub.runnerdetailtofile.service.RunnerDetailToFileService;
import com.prs.hub.statistics.service.StatisticsService;
import com.prs.hub.utils.CromwellUtil;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.HttpClientUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2022/6/14 9:34
 */
@Slf4j
@RestController
@RequestMapping(value = "/prs/hub")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private RunnerDetailService runnerDetailService;

    @Autowired
    private ParameterEnterService parameterEnterService;

    @Autowired
    private RunnerDetailToFileService runnerDetailToFileService;

    @Value("${cromwell.workflows.query.url}")
    private String workflowsQueryUrl;

    @Value("${cromwell.workflows.abort.url}")
    private String workflowsAbortUrl;

    @Value("${cromwell.workflows.status.url}")
    private String workflowsStatusUrl;

    /**
     * 获取runner详情
     * @param req
     * @param res
     * @param userReqDTO
     * @return
     */
    @RequestMapping(value = "/getRunnerStatis", method = RequestMethod.GET)
    @Authorization
    public BaseResult getRunnerStatis(HttpServletRequest req, HttpServletResponse res, @CurrentUser UserReqDTO userReqDTO,RunnerStatisReqDTO runnerStatisReqDTO) {
        log.info("获取runner详情controller开始,userReqDTO=" + JSON.toJSONString(userReqDTO));
        Map<String, Object> resultMap = new HashMap<>();
        try {
            runnerStatisReqDTO.setUserId(Long.valueOf(userReqDTO.getId()));

            log.info("调用statisticsService获取runner详情runnerStatisReqDTO="+JSON.toJSONString(runnerStatisReqDTO));
            IPage<RunnerStatisDTO> jobsPage = statisticsService.queryJobsPage(runnerStatisReqDTO);
            log.info("调用statisticsService获取runner详情runnerStatisDTOList="+JSON.toJSONString(jobsPage));

            resultMap.put("total",jobsPage.getTotal());
            resultMap.put("current",jobsPage.getCurrent());

            List<RunnerStatisDTO> runnerStatisDTOList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(jobsPage.getRecords())){
                runnerStatisDTOList = jobsPage.getRecords();
            }

            resultMap.put("runnerList",runnerStatisDTOList);

            //统计正在运行的数据
            RunnerStatisReqDTO runnerCount = new RunnerStatisReqDTO();
            runnerCount.setStatus(1);

            log.info("调用statisticsService统计runner数据runnerCount="+JSON.toJSONString(runnerCount));
            Long totalResultsCount = statisticsService.count(runnerCount);
            log.info("调用statisticsService统计runner数据totalResultsCount="+totalResultsCount);

            resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("totalResultsCount", totalResultsCount);

        }catch (Exception e){
            log.error("获取runner详情controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","获取runner详情controller异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }

    /**
     * 删除运行记录及其存储结果文件
     * @param uuid
     * @param request
     * @param response
     * @return
     */
    @Authorization
    @RequestMapping(value = "/deleteRunner",method = RequestMethod.GET)
    public BaseResult deleteRunner(@RequestParam("uuid") String uuid, @RequestParam("status") String status, @RequestParam("runnerId") Long  runnerId ,HttpServletRequest request, HttpServletResponse response){
        log.info("删除运行记录及其存储结果文件controller开始,uuid=" +uuid+"\nstatus=" +status +"\nrunnerId=" +runnerId);
        Map<String, Object> resultMap = new HashMap<>();

        if(StringUtils.isEmpty(uuid)||StringUtils.isEmpty(status)){
            log.info("删除运行记录及其存储结果文件controller必传参数为空");
            resultMap.put("code", ResultCodeEnum.EMPTY.getCode());
            resultMap.put("msg" ,"删除运行记录及其存储结果文件controller必传参数为空");
            return BaseResult.ok("接口调用成功",resultMap);
        }

        try {
            //根据uuid获取runnerDeatil
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setWorkflowExecutionUuid(uuid);

            RunnerDetail runnerDetail = runnerDetailService.selectRunnerDetail(runnerStatisReqDTO);

            if(runnerDetail == null){
                resultMap.put("code", ResultCodeEnum.EMPTY.getCode());
                resultMap.put("msg" ,"对应uuid的runner数据不存在");
                return BaseResult.ok("接口调用成功",resultMap);
            }
            if(status.equals("In progress")||status.equals("Not started")){
                //根据uuid中止工作流
                //运行状态 3:Succeeded, 2:failed,1:Running,0:Submitted
                String statusNow = CromwellUtil.workflowsStatus(workflowsStatusUrl,uuid);

                if("Running".equals(statusNow)||"Submitted".equals(statusNow)){
                    CromwellUtil.workflowsAbort( workflowsAbortUrl,uuid);
                }
            }

            //删除该条数据，当存在resultPath时删除运行结果文件
            String resultPath = runnerDetail.getResultPath();
            if(StringUtils.isNotEmpty(resultPath)){
                //删除文件夹
                String filePath = resultPath.substring(0,resultPath.indexOf(uuid))+uuid+"/";
                log.info("删除运行结果文件filePath="+filePath);
                Boolean delFileFlag = FileUtil.delAllFile(filePath);
                log.info("删除运行结果文件delFileFlag="+delFileFlag);
            }

            //删除runner数据
            Boolean delRunnerFlag = runnerDetailService.deleteRunnerDetail(runnerStatisReqDTO);
            log.info("删除runner数据delRunnerFlag="+delRunnerFlag);

            //删除关联表数据
            RunnerDetailToFileReqDTO runnerDetailToFileReqDTO = new RunnerDetailToFileReqDTO();
            runnerDetailToFileReqDTO.setRunnerId(runnerId);
            Boolean delRdtf = runnerDetailToFileService.delete(runnerDetailToFileReqDTO);
            log.info("删除关联表数据delRdtf="+delRdtf);

            //根据uuid 删除 parameterEnterDeatil
            ParameterEnterReqDTO parameterEnterReqDTO = new ParameterEnterReqDTO();
            parameterEnterReqDTO.setWorkflowExecutionUuid(uuid);

            Boolean delparameterEnterFlag = parameterEnterService.deleteParameterEnter(parameterEnterReqDTO);
            log.info("删除parameterEnter数据delparameterEnterFlag="+delparameterEnterFlag);

            resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("msg","删除运行记录及其存储结果文件成功");

        }catch (Exception e){
            log.error("删除运行记录及其存储结果文件controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","删除运行记录及其存储结果文件controller异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }

    /**
     * 根据uuid中止工作流
     * @param uuid
     * @param request
     * @param response
     * @return
     */
    @Authorization
    @RequestMapping(value = "/abortRunner",method = RequestMethod.GET)
    public BaseResult abortRunner(@RequestParam("uuid") String uuid,HttpServletRequest request, HttpServletResponse response){
        log.info("中止工作流controller开始,uuid=" +uuid);
        Map<String, Object> resultMap = new HashMap<>();

        if(StringUtils.isEmpty(uuid)){
            log.info("中止工作流controller必传参数为空");
            resultMap.put("code", ResultCodeEnum.EMPTY.getCode());
            resultMap.put("msg" ,"中止工作流controller必传参数为空");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        Boolean flag = false;
        try {
            //根据uuid获取runnerDeatil
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setWorkflowExecutionUuid(uuid);

            RunnerDetail runnerDetail = runnerDetailService.selectRunnerDetail(runnerStatisReqDTO);

            if(runnerDetail == null){
                resultMap.put("code", ResultCodeEnum.EMPTY.getCode());
                resultMap.put("msg" ,"对应uuid的runner数据不存在");
                return BaseResult.ok("接口调用成功",resultMap);
            }
            //根据uuid中止工作流
            //运行状态 4:中止 3:Succeeded, 2:failed,1:Running,0:Submitted
            //查询当前运行状态
            String statusNow = CromwellUtil.workflowsStatus(workflowsStatusUrl,uuid);

            if("Running".equals(statusNow)||"Submitted".equals(statusNow)){
                flag = CromwellUtil.workflowsAbort(workflowsAbortUrl,uuid);
            }

            //更新数据
            if(flag){
                RunnerStatisReqDTO runnerStatisUpdate = new RunnerStatisReqDTO();
                runnerStatisUpdate.setWorkflowExecutionUuid(uuid);
                runnerStatisUpdate.setStatus(4);
                log.info("调用runnerDetailService更新runner数据runnerStatisUpdate="+JSON.toJSONString(runnerStatisUpdate));
                Boolean resFlag = runnerDetailService.updateRunnerDetail(runnerStatisUpdate);
                log.info("调用runnerDetailService更新runner数据结束resFlag="+resFlag);
            }
            resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("msg","中止工作流成功");

        }catch (Exception e){
            log.error("中止工作流controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("flag",flag);
            resultMap.put("msg","中止工作流controller异常");
            log.info("中止工作流controller结果resultMap="+JSONObject.toJSONString(resultMap));
            return BaseResult.ok("接口调用成功",resultMap);
        }
        resultMap.put("flag",flag);
        log.info("中止工作流controller结果resultMap="+JSONObject.toJSONString(resultMap));
        return BaseResult.ok("接口调用成功",resultMap);
    }


}
