package com.prs.hub.algorithms.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.algorithms.dto.AlgorithmReqDTO;
import com.prs.hub.algorithms.dto.AlgorithmResDTO;
import com.prs.hub.algorithms.dto.AlgorithmsReqDTO;
import com.prs.hub.algorithms.service.AlgorithmsService;
import com.prs.hub.algorithms.service.ParameterEnterService;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.entity.PrsFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private ParameterEnterService parameterEnterService;
    /**
     * 获取算法详情
     * @param req
     * @param res
     * @param userReqDTO
     * @return
     */
    @RequestMapping(value = "/getAlgorithmsInfo", method = RequestMethod.GET)
    @Authorization
    public BaseResult getAlgorithmsInfo(HttpServletRequest req, HttpServletResponse res, @CurrentUser UserReqDTO userReqDTO, @RequestParam("type") String type){
        log.info("获取算法详情controller开始,userReqDTO="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        try {
            AlgorithmReqDTO algorithmReqDTO = new AlgorithmReqDTO();
            algorithmReqDTO.setType(type);
            List<AlgorithmResDTO> algorithmResDTOList = algorithmsService.queryAlgorithmsDetails(algorithmReqDTO);
            resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("data" , algorithmResDTOList);
            log.info("获取算法详情controller结束,resultMap="+JSON.toJSONString(resultMap));
        }catch (Exception e){
            log.error("获取算法详情controller异常",e);
            resultMap.put("code", ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg" ,"获取算法详情controller异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);

    }

    /**
     * 用户设置参数落库
     * @param userReqDTO
     * @return
     */
    @RequestMapping(value = "/setParametersInfo", method = RequestMethod.POST)
    @Authorization
    public BaseResult setParametersInfo(@CurrentUser UserReqDTO userReqDTO, @RequestBody AlgorithmsReqDTO algorithmsReqDTO){
        log.info("用户设置参数落库controller开始,algorithmsReqDTO="+ JSON.toJSON(algorithmsReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        List<AlgorithmReqDTO> algorithmReqDTOList = algorithmsReqDTO.getAlgorithmList();
        Long fileGWASId = algorithmsReqDTO.getFileGWASId();
        if(CollectionUtils.isEmpty(algorithmReqDTOList)||fileGWASId==null){
            log.info("用户设置参数落库controller必传参数为空");
            resultMap.put("code", ResultCodeEnum.EMPTY.getCode());
            resultMap.put("msg" ,"用户设置参数落库controller必传参数为空");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        try {

            Boolean flag = parameterEnterService.setParametersInfo(algorithmsReqDTO);
            resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("data" ,flag);
        }catch (Exception e){
            log.error("用户设置参数落库controller异常",e);
            resultMap.put("code", ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg" ,"用户设置参数落库controller异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        log.info("用户设置参数落库controller结束,resultMap="+JSON.toJSONString(resultMap));
        return BaseResult.ok("接口调用成功",resultMap);
    }
}
