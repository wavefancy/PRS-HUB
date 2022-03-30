package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.bo.AlgorithmsSpecialBo;
import com.prs.hub.practice.dto.AlgorithmsDTO;
import com.prs.hub.practice.mapper.AlgorithmsSpecial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/30 15:50
 */
@Service
public class AlgorithmsSpecialBoImpl  implements AlgorithmsSpecialBo {
    @Autowired
    private AlgorithmsSpecial algorithmsSpecial;

    @Override
    public List<AlgorithmsDTO> queryAlgorithmsDetails() {
        return algorithmsSpecial.queryAlgorithmsDetails(null);
    }
}
