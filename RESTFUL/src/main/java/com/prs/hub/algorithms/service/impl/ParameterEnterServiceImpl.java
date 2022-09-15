package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.algorithms.dto.AlgorithmReqDTO;
import com.prs.hub.algorithms.dto.AlgorithmsReqDTO;
import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;
import com.prs.hub.algorithms.service.ParameterEnterService;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.bo.AlgorithmsBo;
import com.prs.hub.practice.bo.ParameterEnterBo;
import com.prs.hub.practice.bo.RunnerDetailBo;
import com.prs.hub.practice.entity.*;
import com.prs.hub.sftpsystem.dto.SFTPPropertiesDTO;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.HttpClientUtil;
import com.prs.hub.utils.MultipartFileToFileUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private SFTPPropertiesDTO config;
    @Autowired
    private AlgorithmsBo algorithmsBo;
    @Autowired
    private RunnerDetailBo runnerDetailBo;
    @Autowired
    private FileService fileService;
    /**
     * Cromwell服务访问地址
     */
    @Value("${cromwell.workflows.url}")
    private String cromwellUrl;

    /**
     * 保存用户设置参数
     * @param algorithmsReqDTO
     * @return
     */
    @Override
    public Boolean setParametersInfo(AlgorithmsReqDTO algorithmsReqDTO) throws Exception {
        log.info("保存用户设置参数开始palgorithmsReqDTOList="+ JSON.toJSONString(algorithmsReqDTO));
        List<ParameterEnter> parameterEnters = new ArrayList<>();
        List<AlgorithmReqDTO> algorithmReqDTOList = algorithmsReqDTO.getAlgorithmList();
        String jobName = algorithmsReqDTO.getJobName();
        if(CollectionUtils.isEmpty(algorithmReqDTOList)){
            log.info("保存用户设置参数结束，传入数据为空");
            return false;
        }

        //当前系统时间
        LocalDateTime now = LocalDateTime.now();

        Long fileGWASId = algorithmsReqDTO.getFileGWASId();
        //根据fileGWASId获取file信息
        PrsFile prsFile = fileService.getFileById(fileGWASId.toString());

        // 1组装上传参数文件 2调用工作流接口
        for (AlgorithmReqDTO algorithmReqDTO : algorithmReqDTOList) {
            //1组装上传参数文件，
            Map<String,Object> resMap = this.setInputParameterFile(algorithmReqDTO,parameterEnters,prsFile,now);

            //2调用工作流接口存储runner_detail数据
            String cromwellId = this.submitWorkflow(resMap,prsFile,now,jobName);

            if(StringUtils.isEmpty(cromwellId)){
                log.info("工作流运行失败");
                return false;
            }

            // 记录工作流id
            for (int i = 0 ; i < parameterEnters.size() ; i++){
                ParameterEnter parameterEnter = parameterEnters.get(i);
                if(resMap.get("algorithmsId") == parameterEnter.getAlgorithmsId()){
                    parameterEnter.setWorkflowExecutionUuid(cromwellId);
                    parameterEnters.set(i,parameterEnter);
                }
            }
            //删除本地临时文件
//            MultipartFileToFileUtil.delteJTempFile(inputFile);

        }
        if(CollectionUtils.isEmpty(parameterEnters)){
            log.info("保存用户设置参数结束，传入parameterEnters数据为空");
            return false;
        }

        //3保存用户填写的参数
        log.info("调用bo保存用户设置参数parameterEnters="+JSON.toJSONString(parameterEnters));
        Boolean flag = parameterEnterBo.saveBatch(parameterEnters);
        log.info("调用bo保存用户设置参数结束flag="+flag);
        log.info("保存用户设置参数结束flag="+flag);

        return flag;
    }

    /**
     * 组装inputFile，填充parameter_enter表存储数据对象
     * @param algorithmReqDTO 页面传入参数
     * @param parameterEnters parameter_enter表存储数据封装对象
     * @param prsFile 上传GWAS文件信息
     * @param now 当前系统时间
     */
    private Map<String,Object> setInputParameterFile(AlgorithmReqDTO algorithmReqDTO,List<ParameterEnter> parameterEnters,PrsFile prsFile,LocalDateTime now) {
        log.info("组装inputFile，填充parameter_enter表存储数据对象入参：algorithmReqDTO="+JSON.toJSONString(algorithmReqDTO)
                +"\nparameterEnters="+JSON.toJSONString(parameterEnters)
                +"\nnow="+now.toString());

        HashMap<String,Object> resMap = new HashMap<>();

        String filePath = prsFile.getFilePath();
        //页面上传GWAS文件完整地址
        String uploadGWASFilePath =filePath+prsFile.getFileName()+prsFile.getFileSuffix();

        List<ParameterEnterReqDTO> parameterEnterReqList = algorithmReqDTO.getParameters();
        String name = algorithmReqDTO.getName();

        //根据algorithms_id获取algorithms信息
        Algorithms algorithms = algorithmsBo.getById(algorithmReqDTO.getId());
        log.info("根据algorithms_id获取algorithms信息Algorithms="+JSON.toJSONString(algorithms));
        //input文件模板
        String fixedParameter = algorithms.getFixedParameter();
        //算法wdl脚本地址
        String wdlPath = algorithms.getWdlPath();
        resMap.put("wdlPath",wdlPath);

        Long algorithmsId =  algorithms.getId();
        //算法id
        resMap.put("algorithmsId",algorithmsId);

        JSONObject jsonObject = null;
        if (StringUtils.isNotEmpty(fixedParameter)){
            jsonObject = JSON.parseObject(fixedParameter);
        }else {
            jsonObject = new JSONObject();
        }
        String fileName = name+"_input.json";
        for (ParameterEnterReqDTO parameterEnterReqDTO: parameterEnterReqList) {
            ParameterEnter parameterEnter = new ParameterEnter();
            parameterEnter.setAlgorithmsId(algorithmsId);
            parameterEnter.setParameterName(parameterEnterReqDTO.getName());
            parameterEnter.setParameterValue(parameterEnterReqDTO.getValue());
            parameterEnter.setCreatedUser("system");
            parameterEnter.setCreatedDate(now);
            parameterEnter.setModifiedUser("system");
            parameterEnter.setModifiedDate(now);
            parameterEnter.setIsDelete(0);
            parameterEnters.add(parameterEnter);

            String[] valArr = parameterEnterReqDTO.getValue().split(",");

            //获取参数类型
            String type = (String)jsonObject.get(name+"."+parameterEnterReqDTO.getName()+"_1_type");
            //参数类型为int float时
            if(StringUtils.isNotEmpty(type) && (type.indexOf("float") != -1 || type.indexOf("Float") != -1)){
                float[] valFloatArr = new float[valArr.length];
                for (int i = 0;i < valArr.length;i++){
                    if(Float.valueOf(valArr[i]) != null){
                        valFloatArr[i] = Float.valueOf(valArr[i].trim()).floatValue();
                    }
                }
                //拼装json
                jsonObject.put(name+"."+parameterEnterReqDTO.getName()+"_1_value",valFloatArr);
            }else if(StringUtils.isNotEmpty(type) && (type.indexOf("int") != -1 || type.indexOf("Int") != -1)){
                int[] valIntArr = new int[valArr.length];
                for (int i = 0;i < valArr.length;i++){
                    if(Integer.valueOf(valArr[i]) != null){
                        valIntArr[i] = Integer.valueOf(valArr[i].trim()).intValue();
                    }
                }
                //拼装json
                jsonObject.put(name+"."+parameterEnterReqDTO.getName()+"_1_value",valIntArr);
            }else{
                //拼装json
                jsonObject.put(name+"."+parameterEnterReqDTO.getName()+"_1_value",valArr);
            }

        }
        jsonObject.put(name+"."+"summary_statistic_2",uploadGWASFilePath);

        //参数文件地址
        String inputPath = filePath+System.currentTimeMillis()+fileName;
        log.info("将参数写入文件中inputPath="+inputPath);
        FileUtil.writerJsonFile(inputPath,jsonObject);
        resMap.put("inputPath",inputPath);

        log.info("组装参数文件方法返回resMap="+JSON.toJSONString(resMap));
        return resMap;
    }

    /**
     * 调用工作流接口存储runner_detail数据
     * @param resMap 组装上传参数文件返回结果
     * @param prsFile 上传GWAS文件信息
     * @param now 当前系统时间
     * @param jobName
     * @return
     */
    private String submitWorkflow(Map<String,Object> resMap,PrsFile prsFile,LocalDateTime now,String jobName) {

        log.info("调用工作流接口存储runner_detail数据入参：resMap="+JSON.toJSONString(resMap)
                +"\njobName="+jobName
                +"\nnow="+now.toString());

        File inputFile = new File((String) resMap.get("inputPath"));
//            File wdlFile = new File("E:\\temp\\files\\P+T.wdl");
        File wdlFile = new File((String) resMap.get("wdlPath"));

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
            RunnerDetail runnerDetail = new RunnerDetail();
            runnerDetail.setJobName(jobName);
            runnerDetail.setWorkflowExecutionUuid(cromwellId);//工作流uuid
            runnerDetail.setUserId(prsFile.getUserId());
            runnerDetail.setStatus(0);//运行状态 4:Finish, 3:Project at risk ,1:In progress,0:Not started
            runnerDetail.setProgress(0);//运行进度 0-100
            runnerDetail.setCreatedUser("system");
            runnerDetail.setCreatedDate(now);
            runnerDetail.setModifiedUser("system");
            runnerDetail.setModifiedDate(now);
            runnerDetail.setIsDelete(0);
            log.info("保存工作流运行数据开始runnerDetail="+JSON.toJSONString(runnerDetail));
            Boolean runnerFlag = runnerDetailBo.save(runnerDetail);
            log.info("保存工作流运行数据结束runnerFlag="+runnerFlag);
        }

        return cromwellId;
    }

    /**
     * 删除录入参数记录
     * @param parameterEnterReqDTO
     * @return
     */
    @Override
    public Boolean deleteParameterEnter(ParameterEnterReqDTO parameterEnterReqDTO){
        log.info("删除录入参数记录parameterEnterReqDTO="+JSON.toJSONString(parameterEnterReqDTO));
        Boolean flag = false;
        QueryWrapper<ParameterEnter> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(parameterEnterReqDTO.getWorkflowExecutionUuid())){
            queryWrapper.eq("workflow_execution_uuid",parameterEnterReqDTO.getWorkflowExecutionUuid());
        }
        if(parameterEnterReqDTO.getId() != null){
            queryWrapper.eq("id",parameterEnterReqDTO.getId());
        }
        if(parameterEnterReqDTO.getFileId() != null){
            queryWrapper.eq("file_id",parameterEnterReqDTO.getFileId());
        }
        try {
            log.info("删除录入参数记录queryWrapper="+JSON.toJSONString(queryWrapper));
            flag = parameterEnterBo.remove(queryWrapper);
        }catch (Exception e){
            log.error("删除录入参数记录异常",e);
        }
        log.info("删除录入参数记录flag"+flag);
        return flag;
    }
}
