package com.prs.hub.algorithms.service;

import com.prs.hub.practice.entity.Algorithms;
import com.prs.hub.practice.entity.Parameter;

/**
 * @author fanshupeng
 * @create 2022/3/31 19:11
 */
public interface ParameterService {
    /**
     * 根据id查询算法参数
     * @param id
     * @return
     */
    Parameter queryParameterById(Long id);
}
