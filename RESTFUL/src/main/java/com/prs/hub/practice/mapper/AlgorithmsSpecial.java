package com.prs.hub.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prs.hub.practice.dto.AlgorithmsDTO;
import com.prs.hub.practice.entity.Algorithms;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/30 15:22
 */
@Mapper
public interface AlgorithmsSpecial extends BaseMapper<Algorithms> {
    /**
     * 查询算法详细信息
     * @param algorithms
     * @return
     */
    List<AlgorithmsDTO> queryAlgorithmsDetails(Algorithms algorithms);
}
