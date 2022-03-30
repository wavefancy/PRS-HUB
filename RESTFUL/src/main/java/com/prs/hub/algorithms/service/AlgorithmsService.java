package com.prs.hub.algorithms.service;

import com.prs.hub.algorithms.dto.AlgorithmsResDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/25 15:55
 */
public interface AlgorithmsService {
    /**
     * 查询算法详细信息
     * @return
     */
    List<AlgorithmsResDTO> queryAlgorithmsDetails();
}
