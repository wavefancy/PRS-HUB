package com.prs.hub.statistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/6/14 9:35
 */
public interface StatisticsService {
    List<RunnerStatisDTO> getRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO);
    /**
     * 统计runner数据
     * @param runnerStatisReqDTO
     * @return
     */
    Long count(RunnerStatisReqDTO runnerStatisReqDTO);
    /**
     * 分页查询
     * @param runnerStatisReqDTO
     * @return
     */
    IPage<RunnerStatisDTO> queryJobsPage(RunnerStatisReqDTO runnerStatisReqDTO);

    /**
     * 根据工作流id修改工作流状态
     * @param uuid
     * @param status
     * @return
     */
    boolean changeRunnerDetailStatusByUuid(String uuid,String status);
}
