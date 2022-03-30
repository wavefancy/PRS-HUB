package com.prs.hub.practice.bo;

import com.prs.hub.practice.dto.AlgorithmsDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/30 15:49
 */
public interface AlgorithmsSpecialBo  {
        /**
         * 查询算法详细信息
         * @return
         */
        List<AlgorithmsDTO> queryAlgorithmsDetails();
}
