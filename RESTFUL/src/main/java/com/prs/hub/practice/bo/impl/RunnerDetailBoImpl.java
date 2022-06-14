package com.prs.hub.practice.bo.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.practice.mapper.RunnerDetailMapper;
import com.prs.hub.practice.bo.RunnerDetailBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 跑数据详情表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-06-10
 */
@Service
public class RunnerDetailBoImpl extends ServiceImpl<RunnerDetailMapper, RunnerDetail> implements RunnerDetailBo {
    @Autowired
    private RunnerDetailMapper runnerDetailMapper;
    @Override
    public List<RunnerDetail> selectList(QueryWrapper<RunnerDetail> queryWrapper){
        return runnerDetailMapper.selectList(queryWrapper);
    }
}
