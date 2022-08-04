package com.prs.hub.practice.bo;

import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;

import java.util.List;

/**
 * <p>
 * 跑数据详情表 服务类
 * </p>
 *
 * @author fansp
 * @since 2022-06-10
 */
public interface RunnerDetailSpecialBo {
    /**
     * 查询runner详细信息
     * @param runnerDetail
     * @return
     */
    List<RunnerStatisDTO> queryRunnerDetails(RunnerDetail runnerDetail);
}
