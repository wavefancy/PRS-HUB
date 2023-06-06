package com.prs.hub.algorithms.service;

import com.prs.hub.algorithms.dto.AlgorithmsParameterDTO;
import com.prs.hub.algorithms.dto.SubmitWorkflowResDTO;

/**
 * @author fanshupeng
 * @create 2023/4/13 14:58
 */
public interface AlgorithmsParameterService {

    /**
     * 提交工作流
     * @param algorithmsParameterDTO
     * @return
     */
    SubmitWorkflowResDTO submitWorkflow(AlgorithmsParameterDTO algorithmsParameterDTO) throws Exception;

}
