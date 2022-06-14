package com.prs.hub.statistics.service;

import com.prs.hub.practice.entity.RunnerDetail;

/**
 * @author fanshupeng
 * @create 2022/6/14 9:35
 */
public interface StatisticsService {
    RunnerDetail getRunnerDetail(Long userId,Long fileId);
}
