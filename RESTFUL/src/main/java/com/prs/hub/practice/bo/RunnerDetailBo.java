package com.prs.hub.practice.bo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.prs.hub.practice.entity.RunnerDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 跑数据详情表 服务类
 * </p>
 *
 * @author fansp
 * @since 2022-06-10
 */
public interface RunnerDetailBo extends IService<RunnerDetail> {
    /**
     * 按条件查询数据
     * @param queryWrapper
     * @return
     */
    List<RunnerDetail> selectList(QueryWrapper<RunnerDetail> queryWrapper);
}
