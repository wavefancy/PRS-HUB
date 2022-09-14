package com.prs.hub.runnerdetailtofile.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.prs.hub.practice.bo.RunnerDetailToFileBo;
import com.prs.hub.practice.entity.RunnerDetailToFile;
import com.prs.hub.runnerdetailtofile.dto.RunnerDetailToFileReqDTO;
import com.prs.hub.runnerdetailtofile.service.RunnerDetailToFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanshupeng
 * @create 2022/9/14 17:09
 */
@Slf4j
@Service
public class RunnerDetailToFileServiceImpl implements RunnerDetailToFileService {

    @Autowired
    private RunnerDetailToFileBo runnerDetailToFileBo;

    /**
     * 删除RunnerDetailToFile关联表数据
     * @param runnerDetailToFileReqDTO
     * @return
     */
    @Override
    public boolean delete(RunnerDetailToFileReqDTO runnerDetailToFileReqDTO) {
        log.info("删除RunnerDetailToFile关联表数据runnerDetailToFileReqDTO="+ JSON.toJSONString(runnerDetailToFileReqDTO));
        QueryWrapper<RunnerDetailToFile> queryWrapper = new QueryWrapper<>();

        if(runnerDetailToFileReqDTO.getId() != null){
            queryWrapper.eq("id",runnerDetailToFileReqDTO.getId());
        }
        if(runnerDetailToFileReqDTO.getRunnerId() != null){
            queryWrapper.eq("runnerId",runnerDetailToFileReqDTO.getRunnerId());
        }

        log.info("删除RunnerDetailToFile关联表数据开始");
        Boolean resFlag = runnerDetailToFileBo.remove(queryWrapper);
        log.info("删除RunnerDetailToFile关联表数据结束resFlag="+resFlag);

        return resFlag;
    }
}
