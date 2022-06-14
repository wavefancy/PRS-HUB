package com.prs.hub.practice.bo.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.prs.hub.practice.entity.MetadataEntry;
import com.prs.hub.practice.mapper.MetadataEntryMapper;
import com.prs.hub.practice.bo.MetadataEntryBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-05-15
 */
@Service
public class MetadataEntryBoImpl extends ServiceImpl<MetadataEntryMapper, MetadataEntry> implements MetadataEntryBo {

    @Autowired
    private MetadataEntryMapper metadataEntryMapper;

    @Override
    public List<MetadataEntry> selectList(QueryWrapper<MetadataEntry> queryWrapper) {
        return metadataEntryMapper.selectList(queryWrapper);
    }
}
