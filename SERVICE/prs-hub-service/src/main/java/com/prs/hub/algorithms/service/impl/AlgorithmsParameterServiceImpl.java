package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prs.hub.algorithms.dto.AlgorithmsParameterDTO;
import com.prs.hub.algorithms.service.AlgorithmsParameterService;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.HttpClientUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanshupeng
 * @create 2023/4/13 14:58
 */
@Service
@Slf4j
public class AlgorithmsParameterServiceImpl implements AlgorithmsParameterService {

    /**
     * Cromwell服务访问地址
     */
    @Value("${cromwell.workflows.run.url}")
    private String cromwellUrl;
    @Value("${temporary.file.path}")
    private String temporaryFilePath;

    /**
     * 提交工作流接口
     * @param algorithmsParameterDTO
     * @return
     */
    @Override
    public String submitWorkflow(AlgorithmsParameterDTO algorithmsParameterDTO) throws Exception  {
        log.info("提交工作流开始algorithmsParameterDTO="+ JSON.toJSONString(algorithmsParameterDTO));

        //wdl脚本地址
        String wdlPath = algorithmsParameterDTO.getWdlPath();


        //参数临时文件地址
        String inputFilePath = temporaryFilePath+System.currentTimeMillis()+"_inputFile.json";
        JSONObject inputJson = algorithmsParameterDTO.getInputJson();
        log.info("将参数写入临时文件地址中inputFilePath="+inputFilePath);
        FileUtil.writerJsonFile(inputFilePath,inputJson);

        File inputFile = new File(inputFilePath);
        File wdlFile = new File(wdlPath);

        Map<String,File> fileMap = new HashMap<>();
        fileMap.put("workflowInputs",inputFile);
        fileMap.put("workflowSource",wdlFile);

        log.info("访问cromwell提交工作流");
        String resultmsg = HttpClientUtil.httpClientUploadFileByfile(fileMap,cromwellUrl);
        log.info("访问cromwell提交工作流返回结果"+JSON.toJSONString(resultmsg));
        String cromwellId = null;
        if(StringUtils.isNotEmpty(resultmsg)){
            JSONObject  cromwellResult = JSON.parseObject(resultmsg);
            cromwellId = cromwellResult.get("id").toString();
        }
        //删除临时文件
        FileUtil.delAllFile(inputFilePath);
        log.info("提交工作流结束 cromwellId="+ cromwellId);

        return cromwellId;
    }
}
