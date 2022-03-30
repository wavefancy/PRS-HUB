package com.prs.hub.algorithms.controller;

import com.alibaba.fastjson.JSON;
import com.prs.hub.algorithms.dto.AlgorithmsResDTO;
import com.prs.hub.algorithms.service.AlgorithmsService;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
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
 * @create 2022/3/25 15:44
 */
@Slf4j
@RestController
@RequestMapping(value = "/prs/hub/algorithms")
public class AlgorithmsController {
    @Autowired
    private AlgorithmsService algorithmsService;
    /**
     * 获取算法详情
     * @param req
     * @param res
     * @param userReqDTO
     * @return
     */
    @RequestMapping(value = "/getAlgorithmsInfo", method = RequestMethod.GET)
    @Authorization
    public BaseResult getAlgorithmsInfo(HttpServletRequest req, HttpServletResponse res, @CurrentUser UserReqDTO userReqDTO){
        log.info("获取算法详情controller开始,userReqDTO="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        try {
            List<AlgorithmsResDTO> algorithmsResDTOList = algorithmsService.queryAlgorithmsDetails();
            resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("data" ,algorithmsResDTOList);
            log.info("获取算法详情controller结束,algorithmsResDTOList="+JSON.toJSONString(algorithmsResDTOList));
        }catch (Exception e){
            log.error("获取算法详情controller异常",e);
            resultMap.put("code", ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg" ,"获取算法详情controller异常");
        }
        return BaseResult.ok("成功",resultMap);
    }
}
