package com.prs.hub.statistics.controller;

import com.alibaba.fastjson.JSON;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.statistics.dto.RunnerStatisDTO;
import com.prs.hub.statistics.dto.RunnerStatisReqDTO;
import com.prs.hub.statistics.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    /**
     * 获取runner详情
     * @param req
     * @param res
     * @param userReqDTO
     * @return
     */
    @RequestMapping(value = "/getRunnerStatis", method = RequestMethod.GET)
    @Authorization
    public BaseResult getRunnerStatis(HttpServletRequest req, HttpServletResponse res, @CurrentUser UserReqDTO userReqDTO) {
        log.info("获取runner详情controller开始,userReqDTO=" + JSON.toJSON(userReqDTO));
        Map<String, Object> resultMap = new HashMap<>();
        try {
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setUserId(Long.valueOf(userReqDTO.getId()));
            List<RunnerStatisDTO> runnerStatisDTOList = statisticsService.getRunnerDetail(runnerStatisReqDTO);
            resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("runnerStatisDTOList",runnerStatisDTOList);

        }catch (Exception e){
            log.error("获取runner详情controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","获取runner详情");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }
}
