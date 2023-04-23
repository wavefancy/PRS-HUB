package com.prs.hub.files.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.prs.hub.files.dto.FileParsingDTO;
import com.prs.hub.files.service.FileService;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.HttpClientUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author fanshupeng
 * @create 2023/4/21 15:43
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    /**
     * 目标地址
     */
    @Value("${rsync.destination.path}")
    private  String destinationPath;
    /**
     * Cromwell服务访问地址
     */
    @Value("${cromwell.workflows.run.url}")
    private String cromwellUrl;
    /**
     * 临时文件地址
     */
    @Value("${temporary.file.path}")
    private String temporaryFilePath;
    /**
     * ld文件解析wdl脚本地址地址
     */
    @Value("${ldref.generator.wdl.path}")
    private String ldGenerator;
    /**
     * 调用文件解析工作流,对上传的LD文件进行解析
     * @param fileParsingDTO
     * @return
     */
    @Override
    public String fileParsing(FileParsingDTO fileParsingDTO) {
        log.info("调用文件解析工作流,对上传的LD文件进行解析入参fileParsingDTO：{}" ,JSONObject.toJSONString(fileParsingDTO));
        //调用文件解析工作流,对上传的LD文件进行解析
        //组装input文件
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ldref_generator.pgenfile_1_value",destinationPath+fileParsingDTO.getDestinationFileName());
        jsonObject.put("ldref_generator.pop_1_value",fileParsingDTO.getPopValue());
        log.info("进行LD文件解析,组装input文件"+jsonObject.toJSONString());

        //参数文件临时地址
        String messageId = UUID.randomUUID().toString();
        String inputFilePath = temporaryFilePath+messageId+"_inputFile.json";
        log.info("将参数写入文件中inputFilePath="+inputFilePath);
        FileUtil.writerJsonFile(inputFilePath,jsonObject);

        File inputFile = new File(inputFilePath);
        File wdlFile = new File(ldGenerator);

        Map<String,File> fileMap = new HashMap<>();
        fileMap.put("workflowInputs",inputFile);
        fileMap.put("workflowSource",wdlFile);

        log.info("进行LD文件解析访问cromwell提交工作流");
        String resultmsg = HttpClientUtil.httpClientUploadFileByfile(fileMap,cromwellUrl);
        log.info("进行LD文件解析访问cromwell提交工作流返回结果"+JSONObject.toJSONString(resultmsg));
        String cromwellId = null;
        if(StringUtils.isNotEmpty(resultmsg)){
            JSONObject cromwellResult = JSONObject.parseObject(resultmsg);
            cromwellId = cromwellResult.get("id").toString();
        }

        //删除临时文件
        FileUtil.delAllFile(inputFilePath);
        log.info("调用文件解析工作流,对上传的LD文件进行解析出参cromwellId：{}" ,cromwellId);
        return cromwellId;
    }
}
