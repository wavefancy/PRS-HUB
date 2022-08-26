package com.prs.hub.practice.bo.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prs.hub.practice.bo.RunnerDetailSpecialBo;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.practice.mapper.RunnerDetailSpecial;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/6/23 15:07
 */
@Service
@Slf4j
public class RunnerDetailSpecialBoImpl implements RunnerDetailSpecialBo {

    @Autowired
    private RunnerDetailSpecial runnerDetailSpecial;

    @Override
    public List<RunnerStatisDTO> queryRunnerDetails(RunnerDetail runnerDetail) {
        return runnerDetailSpecial.queryRunnerDetails(runnerDetail);
    }
    public IPage<RunnerStatisDTO> queryJobsPage(RunnerDetail runnerDetail ,Long size,Long current) {
        Page<RunnerStatisDTO> runnerDetailIPage = new Page<>();
        runnerDetailIPage.setCurrent(current);
        runnerDetailIPage.setSize(size);
        IPage<RunnerStatisDTO> runnerStatisDTOIPage = runnerDetailSpecial.jobsPage(runnerDetailIPage,runnerDetail.getUserId());
        log.info("分页查询runnerStatisDTOIPage="+JSON.toJSONString(runnerStatisDTOIPage));
        return runnerStatisDTOIPage;
    }
}
