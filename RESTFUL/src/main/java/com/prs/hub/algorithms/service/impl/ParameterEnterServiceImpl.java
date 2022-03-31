package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.prs.hub.algorithms.dto.AlgorithmsReqDTO;
import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;
import com.prs.hub.algorithms.service.ParameterEnterService;
import com.prs.hub.practice.bo.ParameterEnterBo;
import com.prs.hub.practice.entity.ParameterEnter;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author fanshupeng
 * @create 2022/3/31 18:56
 */
@Slf4j
@Service
public class ParameterEnterServiceImpl implements ParameterEnterService {
    @Autowired
    private ParameterEnterBo parameterEnterBo;

    /**
     * 保存用户设置参数
     * @param parameterEnterReqDTO
     * @return
     */
    @Override
    public Boolean setParametersInfo(ParameterEnterReqDTO parameterEnterReqDTO) {
        log.info("保存用户设置参数开始algorithmsReqDTO="+ JSON.toJSONString(parameterEnterReqDTO));
        ParameterEnter parameterEnter = new ParameterEnter();
        String id = parameterEnterReqDTO.getId();
        if(StringUtils.isNotEmpty(id)){
            parameterEnter.setParameterId(Long.valueOf(id));
        }
        String fileId = parameterEnterReqDTO.getFileId();
        if(StringUtils.isNotEmpty(fileId)){
            parameterEnter.setFileId(Long.valueOf(fileId));
        }
        LocalDateTime now = LocalDateTime.now();
        parameterEnter.setValue(parameterEnterReqDTO.getValue());
        parameterEnter.setCreatedUser("system");
        parameterEnter.setCreatedDate(now);
        parameterEnter.setModifiedUser("system");
        parameterEnter.setModifiedDate(now);
        parameterEnter.setIsDelete(0);

        parameterEnterBo.save(parameterEnter);
        log.info("保存用户设置参数结束");
        return null;
    }
}
