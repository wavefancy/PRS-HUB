package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.algorithms.dto.AlgorithmReqDTO;
import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;
import com.prs.hub.algorithms.service.ParameterEnterService;
import com.prs.hub.practice.bo.ParameterEnterBo;
import com.prs.hub.practice.entity.ParameterEnter;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.MultipartFileToFileUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/31 18:56
 */
@Slf4j
@Service
public class ParameterEnterServiceImpl implements ParameterEnterService {
    @Autowired
    private ParameterEnterBo parameterEnterBo;

    @Autowired
    private SFTPSystemService sftpSystemService;
    /**
     * 保存用户设置参数
     * @param algorithmReqDTOList
     * @return
     */
    @Override
    public Boolean setParametersInfo(List<AlgorithmReqDTO> algorithmReqDTOList, PrsFile prsFile) throws Exception {
        log.info("保存用户设置参数开始palgorithmsReqDTOList="+ JSON.toJSONString(algorithmReqDTOList));
        List<ParameterEnter> parameterEnters = new ArrayList<>();
        if(CollectionUtils.isEmpty(algorithmReqDTOList)){
            log.info("保存用户设置参数结束，传入数据为空");
            return false;
        }
        Long fileId = prsFile.getId();
        //当前系统时间
        LocalDateTime now = LocalDateTime.now();
        for (AlgorithmReqDTO algorithmReqDTO : algorithmReqDTOList) {
            List<ParameterEnterReqDTO> parameterEnterReqList = algorithmReqDTO.getParameters();
            String name = algorithmReqDTO.getName();
            String fileName = name+"_input.json";
            JSONObject jsonObject = new JSONObject();
            for (ParameterEnterReqDTO parameterEnterReqDTO: parameterEnterReqList) {
                ParameterEnter parameterEnter = new ParameterEnter();
                String id = parameterEnterReqDTO.getId();
                if(StringUtils.isNotEmpty(id)){
                    parameterEnter.setParameterId(Long.valueOf(id));
                }
                if(fileId != null){
                    parameterEnter.setFileId(fileId);
                }
                parameterEnter.setValue(parameterEnterReqDTO.getValue());
                parameterEnter.setCreatedUser("system");
                parameterEnter.setCreatedDate(now);
                parameterEnter.setModifiedUser("system");
                parameterEnter.setModifiedDate(now);
                parameterEnter.setIsDelete(0);
                parameterEnters.add(parameterEnter);
                //拼装json
                jsonObject.put("PandT."+parameterEnterReqDTO.getName()+"_list",parameterEnterReqDTO.getValue());
            }
            log.info("将参数写入文件中");
            FileUtil.writerJsonFile(fileName,jsonObject);
            // 将文件上传到指定服务器
            String filePath = prsFile.getFilePath();
            log.info("参数文件上传,targetPath="+filePath+fileName);
            sftpSystemService.uploadFile(filePath+fileName,new FileInputStream(fileName));
            log.info("参数文件上传成功");
            //删除本地临时文件
            MultipartFileToFileUtil.delteTempFile(new File(fileName));

        }
        if(CollectionUtils.isEmpty(parameterEnters)){
            log.info("保存用户设置参数结束，传入parameterEnters数据为空");
            return false;
        }
        log.info("调用bo保存用户设置参数parameterEnters="+JSON.toJSONString(parameterEnters));
        Boolean flag = parameterEnterBo.saveBatch(parameterEnters);
        log.info("调用bo保存用户设置参数结束flag="+flag);
        log.info("保存用户设置参数结束flag="+flag);
        return flag;
    }
}
