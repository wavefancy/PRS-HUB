package com.prs.hub.runnerdetailtofile.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.prs.hub.practice.bo.RunnerDetailToFileBo;
import com.prs.hub.practice.entity.RunnerDetailToFile;
import com.prs.hub.runnerdetailtofile.dto.RunnerDetailToFileReqDTO;
import com.prs.hub.runnerdetailtofile.service.RunnerDetailToFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            queryWrapper.eq("runner_id",runnerDetailToFileReqDTO.getRunnerId());
        }

        log.info("删除RunnerDetailToFile关联表数据开始");
        Boolean resFlag = runnerDetailToFileBo.remove(queryWrapper);
        log.info("删除RunnerDetailToFile关联表数据结束resFlag="+resFlag);

        return resFlag;
    }

    /**
     * 新增或修改
     * @param runnerDetailToFileReqDTO
     * @return
     */
    @Override
    public boolean saveOrUpdate(RunnerDetailToFileReqDTO runnerDetailToFileReqDTO) {
        log.info("新增或修改runnerDetailToFileReqDTO="+JSON.toJSONString(runnerDetailToFileReqDTO));

        RunnerDetailToFile runnerDetailToFile = new RunnerDetailToFile();
        if(runnerDetailToFileReqDTO.getRunnerId() != null){
            runnerDetailToFile.setRunnerId(runnerDetailToFileReqDTO.getRunnerId());
        }
        if(runnerDetailToFileReqDTO.getLdFileId() != null){
            runnerDetailToFile.setLdFileId(runnerDetailToFileReqDTO.getLdFileId());
        }
        if(runnerDetailToFileReqDTO.getGwasFileId() != null){
            runnerDetailToFile.setGwasFileId(runnerDetailToFileReqDTO.getGwasFileId());
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        if(runnerDetailToFileReqDTO.getId() != null){
            runnerDetailToFile.setId(runnerDetailToFileReqDTO.getId());
            runnerDetailToFile.setModifiedDate(localDateTime);
        }else{
            runnerDetailToFile.setCreatedDate(localDateTime);
            runnerDetailToFile.setCreatedUser("system");
            runnerDetailToFile.setModifiedDate(localDateTime);
            runnerDetailToFile.setModifiedUser("system");
            runnerDetailToFile.setIsDelete(0);
        }
        UpdateWrapper<RunnerDetailToFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",runnerDetailToFileReqDTO.getId());

        log.info("调用runnerDetailToFileBo新增或修改开始runnerDetailToFile="+JSON.toJSONString(runnerDetailToFile));
        boolean resFlag = runnerDetailToFileBo.saveOrUpdate(runnerDetailToFile,updateWrapper);
        log.info("调用runnerDetailToFileBo新增或修改结束resFlag="+resFlag);


        return resFlag;
    }

    /**
     * 批量新增数据
     * @param runnerDetailToFileReqDTOList
     * @return
     */
    @Override
    public boolean saveBatch(List<RunnerDetailToFileReqDTO> runnerDetailToFileReqDTOList){
        log.info("批量新增数据runnerDetailToFileReqDTOList="+JSON.toJSONString(runnerDetailToFileReqDTOList));

        List<RunnerDetailToFile> runnerDetailToFiles = new ArrayList<>();
        for (RunnerDetailToFileReqDTO runnerDetailToFileReqDTO:runnerDetailToFileReqDTOList) {
            RunnerDetailToFile runnerDetailToFile = new RunnerDetailToFile();
            if(runnerDetailToFileReqDTO.getRunnerId() != null){
                runnerDetailToFile.setRunnerId(runnerDetailToFileReqDTO.getRunnerId());
            }
            if(runnerDetailToFileReqDTO.getLdFileId() != null){
                runnerDetailToFile.setLdFileId(runnerDetailToFileReqDTO.getLdFileId());
            }
            if(runnerDetailToFileReqDTO.getGwasFileId() != null){
                runnerDetailToFile.setGwasFileId(runnerDetailToFileReqDTO.getGwasFileId());
            }
            LocalDateTime localDateTime = LocalDateTime.now();
            runnerDetailToFile.setCreatedDate(localDateTime);
            runnerDetailToFile.setCreatedUser("system");
            runnerDetailToFile.setModifiedDate(localDateTime);
            runnerDetailToFile.setModifiedUser("system");
            runnerDetailToFile.setIsDelete(0);
            runnerDetailToFiles.add(runnerDetailToFile);
        }

        log.info("runnerDetailToFileBo批量新增数据runnerDetailToFiles="+JSON.toJSONString(runnerDetailToFiles));
        boolean resFlag = runnerDetailToFileBo.saveBatch(runnerDetailToFiles);
        log.info("runnerDetailToFileBo批量新增数据结束resFlag="+resFlag);

        return resFlag;
    }
}
