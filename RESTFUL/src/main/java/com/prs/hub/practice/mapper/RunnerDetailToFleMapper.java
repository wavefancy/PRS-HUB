package com.prs.hub.practice.mapper;

import com.prs.hub.practice.entity.RunnerDetailToFle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 运行job与选择file的关联表 Mapper 接口
 * </p>
 *
 * @author fansp
 * @since 2022-09-13
 */
@Mapper
public interface RunnerDetailToFleMapper extends BaseMapper<RunnerDetailToFle> {

}
