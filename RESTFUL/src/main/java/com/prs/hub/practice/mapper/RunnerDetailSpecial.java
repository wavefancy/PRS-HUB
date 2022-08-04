package com.prs.hub.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 跑数据详情表 Mapper 接口
 * </p>
 *
 * @author fansp
 * @since 2022-06-10
 */
@Mapper
public interface RunnerDetailSpecial extends BaseMapper<RunnerDetail> {
    /**
     * 查询runner详细信息
     * @param runnerDetail
     * @return
     */
    List<RunnerStatisDTO> queryRunnerDetails(RunnerDetail runnerDetail);
}
