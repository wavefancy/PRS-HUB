package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prs.hub.algorithms.dto.AlgorithmsParameterDTO;
import com.prs.hub.algorithms.dto.SubmitWorkflowResDTO;
import com.prs.hub.algorithms.service.AlgorithmsParameterService;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.HttpClientUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
    public SubmitWorkflowResDTO submitWorkflow(AlgorithmsParameterDTO algorithmsParameterDTO) throws Exception  {
        log.info("提交工作流开始algorithmsParameterDTO="+ JSON.toJSONString(algorithmsParameterDTO));
        SubmitWorkflowResDTO submitWorkflowResDTO = new SubmitWorkflowResDTO();
        //wdl脚本地址
        String wdlPath = algorithmsParameterDTO.getWdlPath();


        //参数临时文件地址
        String inputFilePath = temporaryFilePath+System.currentTimeMillis()+"_inputFile.json";
        JSONObject inputJson = algorithmsParameterDTO.getInputJson();
        log.info("将参数写入临时文件地址中inputFilePath="+inputFilePath);
        FileUtil.writerJsonFile(inputFilePath,inputJson);

//        File inputFile = new File(inputFilePath);
//        File wdlFile = new File(wdlPath);
//        File optionsFile = new File("");
        File inputFile = new File("/srv/zhaohq/cromwell/test/NA12878_110X/HNA12878-2.read1.fq.gz.fq2ubam.json");
        File wdlFile = new File("/pmaster/pipeline/cromwell/1_pipeline/fastq2ubam/seq-format-conversion-master/paired-fastq-to-unmapped-bam.wdl");

        //获取options模板并修改结果目录
        JSONObject optionJson  = changeOptions(algorithmsParameterDTO);

        String outputsPath = (String) optionJson.get("final_workflow_outputs_dir");
        submitWorkflowResDTO.setOutputsPath(outputsPath);

        //临时存放options模板地址
        String optionFilePath = temporaryFilePath+System.currentTimeMillis()+"_option.json";
        log.info("将参数写入临时文件地址中optionFilePath="+inputFilePath);
        FileUtil.writerJsonFile(optionFilePath,optionJson);
        File optionsFile = new File(optionFilePath);

        Map<String,File> fileMap = new HashMap<>();
        fileMap.put("workflowInputs",inputFile);
        fileMap.put("workflowSource",wdlFile);
        fileMap.put("workflowOptions",optionsFile);

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
        FileUtil.delAllFile(optionFilePath);
        log.info("提交工作流结束 cromwellId="+ cromwellId);
        submitWorkflowResDTO.setCromwellId(cromwellId);
        return submitWorkflowResDTO;
    }

    /**
     * 修改option文件内容
     * @param algorithmsParameterDTO
     * @return
     */
    private JSONObject changeOptions(AlgorithmsParameterDTO algorithmsParameterDTO) {
        log.info("修改option文件内容入参;{}",JSONObject.toJSONString(algorithmsParameterDTO));

        String userId = algorithmsParameterDTO.getUserId();
        String runnerId = algorithmsParameterDTO.getRunnerId();
        String optionsPath = algorithmsParameterDTO.getOptionsPath();
        JSONObject optionsJson = null;
        try {
            File file = new File(optionsPath);
            String optionsStr = FileUtils.readFileToString(file,"UTF-8");
            log.info("option文件修改前内容:{}",optionsStr);

            optionsStr = optionsStr.replace("**replace**",userId+"_"+runnerId);

            log.info("option文件修改后内容:{}",optionsStr);
            optionsJson = JSON.parseObject(optionsStr);

        }catch (Exception e){
            log.error("修改option文件内容异常:{}",e.getMessage());
            return optionsJson;
        }
        return optionsJson;
    }
}
