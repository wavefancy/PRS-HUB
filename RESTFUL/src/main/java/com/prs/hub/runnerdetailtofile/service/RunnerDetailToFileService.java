package com.prs.hub.runnerdetailtofile.service;

import com.prs.hub.runnerdetailtofile.dto.RunnerDetailToFileReqDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/9/14 17:08
 */
public interface RunnerDetailToFileService {

    boolean delete(RunnerDetailToFileReqDTO runnerDetailToFileReqDTO);

    /**
     * 新增或修改
     * @param runnerDetailToFileReqDTO
     * @return
     */
    boolean saveOrUpdate(RunnerDetailToFileReqDTO runnerDetailToFileReqDTO);

    /**
     * 批量新增数据
     * @param runnerDetailToFileReqDTOList
     * @return
     */
    boolean saveBatch(List<RunnerDetailToFileReqDTO> runnerDetailToFileReqDTOList);
}
