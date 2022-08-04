package com.prs.hub.algorithms.service;

import com.prs.hub.algorithms.dto.AlgorithmReqDTO;
import com.prs.hub.algorithms.dto.AlgorithmsReqDTO;
import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;
import com.prs.hub.practice.entity.PrsFile;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/31 18:56
 */
public interface ParameterEnterService {
    /**
     * 查询算法详细信息
     * @return
     */
    Boolean setParametersInfo(AlgorithmsReqDTO algorithmsReqDTO,PrsFile prsFile) throws Exception;
    /**
     * 删除录入参数记录
     * @param parameterEnterReqDTO
     * @return
     */
    Boolean deleteParameterEnter(ParameterEnterReqDTO parameterEnterReqDTO);
}
