package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.prs.hub.algorithms.dto.AlgorithmsResDTO;
import com.prs.hub.algorithms.dto.ParameterResDTO;
import com.prs.hub.algorithms.service.AlgorithmsService;
import com.prs.hub.practice.bo.AlgorithmsSpecialBo;
import com.prs.hub.practice.dto.AlgorithmsDTO;
import com.prs.hub.practice.dto.ParameterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 算法service
 * @author fanshupeng
 * @create 2022/3/25 16:12
 */
@Slf4j
@Service
public class AlgorithmsServiceImpl implements AlgorithmsService {
    @Autowired
    private AlgorithmsSpecialBo algorithmsSpecialBo;

    @Override
    public List<AlgorithmsResDTO> queryAlgorithmsDetails() {
        log.info("service查询算法详细信息开始");
        log.info("service调用Bo查询算法详细信息开始");
        List<AlgorithmsDTO> algorithmsDTOList = algorithmsSpecialBo.queryAlgorithmsDetails();
        log.info("service调用Bo查询算法详细信息结束algorithmsDTOList="+ JSON.toJSONString(algorithmsDTOList));
        List<AlgorithmsResDTO> algorithmsResDTOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(algorithmsDTOList)){
            for (AlgorithmsDTO algorithmsDTO:algorithmsDTOList) {
                AlgorithmsResDTO algorithmsResDTO = new AlgorithmsResDTO();
                BeanUtils.copyProperties(algorithmsDTO,algorithmsResDTO);
                List<ParameterResDTO> parameterResDTOList = new ArrayList<>();
                for (ParameterDTO parameterDTO:algorithmsDTO.getParameters()) {
                    ParameterResDTO parameterResDTO = new ParameterResDTO();
                    BeanUtils.copyProperties(parameterDTO,parameterResDTO);
                    parameterResDTOList.add(parameterResDTO);
                }
                algorithmsResDTO.setParameters(parameterResDTOList);
                algorithmsResDTOList.add(algorithmsResDTO);
                System.out.println();
            }
        }
        log.info("service查询算法详细信息结束algorithmsResDTOList="+JSON.toJSONString(algorithmsResDTOList));
        return algorithmsResDTOList;
    }
}
