package com.prs.hub.practice.bo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.prs.hub.practice.entity.MetadataEntry;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fansp
 * @since 2022-05-15
 */
public interface MetadataEntryBo extends IService<MetadataEntry> {

    List<MetadataEntry> selectList(QueryWrapper<MetadataEntry> queryWrapper);

}
