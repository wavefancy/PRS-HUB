package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.bo.RunnerDetailSpecialBo;
import com.prs.hub.practice.entity.RunnerDetail;
import com.prs.hub.practice.mapper.RunnerDetailSpecial;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/6/23 15:07
 */
@Service
public class RunnerDetailSpecialBoImpl implements RunnerDetailSpecialBo {

    @Autowired
    private RunnerDetailSpecial runnerDetailSpecial;

    @Override
    public List<RunnerStatisDTO> queryRunnerDetails(RunnerDetail runnerDetail) {
        return runnerDetailSpecial.queryRunnerDetails(runnerDetail);
    }
}
