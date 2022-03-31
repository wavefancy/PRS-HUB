package com.prs.hub.algorithms.service;

import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;

/**
 * @author fanshupeng
 * @create 2022/3/31 18:56
 */
public interface ParameterEnterService {
    /**
     * 查询算法详细信息
     * @return
     */
    Boolean setParametersInfo(ParameterEnterReqDTO parameterEnterReqDTO);
}