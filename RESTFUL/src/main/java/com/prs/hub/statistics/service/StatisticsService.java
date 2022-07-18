package com.prs.hub.statistics.service;

import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.statistics.dto.RunnerStatisDTO;
import com.prs.hub.statistics.dto.RunnerStatisReqDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/6/14 9:35
 */
public interface StatisticsService {
    List<RunnerStatisDTO> getRunnerDetail(RunnerStatisReqDTO runnerStatisReqDTO);
}
