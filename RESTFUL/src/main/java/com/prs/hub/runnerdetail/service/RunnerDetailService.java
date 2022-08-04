package com.prs.hub.runnerdetail.service;

import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;

/**
 * @author fanshupeng
 * @create 2022/7/20 16:54
 */
public interface RunnerDetailService {
    /**
     * 查询数据
     * @param runnerStatisReqDTO
     * @return
     */
    RunnerDetail selectRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO);
    /**
     * 删除数据
     * @param runnerStatisReqDTO
     * @return
     */
    boolean deleteRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO);
    /**
     * 修改数据
     * @param runnerStatisReqDTO
     * @return
     */
    boolean updateRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO);
}
