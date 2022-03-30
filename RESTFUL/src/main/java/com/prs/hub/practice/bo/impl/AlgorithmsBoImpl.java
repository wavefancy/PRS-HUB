package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.Algorithms;
import com.prs.hub.practice.mapper.AlgorithmsMapper;
import com.prs.hub.practice.bo.AlgorithmsBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 算法表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-03-18
 */
@Service
public class AlgorithmsBoImpl extends ServiceImpl<AlgorithmsMapper, Algorithms> implements AlgorithmsBo {

}
