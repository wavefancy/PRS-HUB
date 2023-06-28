package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.algorithms.dto.AlgorithmReqDTO;
import com.prs.hub.algorithms.dto.AlgorithmsReqDTO;
import com.prs.hub.algorithms.dto.ParameterEnterReqDTO;
import com.prs.hub.algorithms.service.ParameterEnterService;
import com.prs.hub.constant.LdEnum;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.bo.AlgorithmsBo;
import com.prs.hub.practice.bo.ParameterEnterBo;
import com.prs.hub.practice.bo.RunnerDetailBo;
import com.prs.hub.practice.entity.*;
import com.prs.hub.rabbitmq.dto.MQMessageDTO;
import com.prs.hub.rabbitmq.service.ProducerService;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.runnerdetail.service.RunnerDetailService;
import com.prs.hub.runnerdetailtofile.dto.RunnerDetailToFileReqDTO;
import com.prs.hub.runnerdetailtofile.service.RunnerDetailToFileService;
import com.prs.hub.sftpsystem.dto.SFTPPropertiesDTO;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.statistics.dto.GWASAndLDFilenameDTO;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private RunnerDetailService runnerDetailService;
    @Autowired
    private RunnerDetailToFileService runnerDetailToFileService;


    /**
     * 发送消息
     */
    @Autowired
    private ProducerService producerService;

    //交换机名称
    @Value("${topic.exchange}")
    private String TOPIC_EXCHANGE;

    @Value("${algorithms.parameter.routing.key}")
    private String parameterRoutingKey;
    private String algorithmsReqDTO;

    /**
     * 保存用户设置参数
     * @param algorithmsReqDTO
     * @return
     */
    @Override
    public Boolean setParametersInfo(AlgorithmsReqDTO algorithmsReqDTO,String userId) throws Exception {
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
        Long fileLDId = algorithmsReqDTO.getFileLDId();
        Map<String,String> singleMap = null;
        Map<String,List<String>> multipleMap = null;
        if(fileGWASId != null || fileLDId != null){
            singleMap = this.getFilePathMap(fileGWASId,fileLDId );
        }else{
            List<GWASAndLDFilenameDTO> gwasAndLDFilenameDTOList =  algorithmsReqDTO.getGwasAndLDFilenameDTOList();
            multipleMap = this.getListFilePathMap(gwasAndLDFilenameDTOList );
        }



        // 1组装上传参数文件 2调用工作流接口
        for (AlgorithmReqDTO algorithmReqDTO : algorithmReqDTOList) {
            //1组装上传参数文件，
            Map<String,Object> resMap = this.setInputParameterFile(algorithmReqDTO,parameterEnters,now,singleMap,multipleMap);
            resMap.put("userId",userId);
            //推送消息保存数据
            this.sendMessageAndSaveRunnerDetail(resMap,algorithmsReqDTO,parameterEnters);
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
     * 发送消息 保存数据
     * @param resMap
     */
    private Boolean sendMessageAndSaveRunnerDetail(Map<String, Object> resMap,AlgorithmsReqDTO algorithmsReqDTO,List<ParameterEnter> parameterEnters) {


        String messageId = UUID.randomUUID().toString();
        //当前系统时间
        LocalDateTime now = LocalDateTime.now();
        //保存数据
        Long runnerId = this.saveRunnerDetail((String) resMap.get("userId"),now,algorithmsReqDTO,messageId);
        resMap.put("runnerId",runnerId.toString());

        for (int i = 0 ; i < parameterEnters.size() ; i++) {
            ParameterEnter parameterEnter = parameterEnters.get(i);
            parameterEnter.setRunnerId(runnerId.toString());
        }

        JSONObject inputJson = new JSONObject(resMap);

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(parameterRoutingKey);
        messageDTO.setTag("prs.hub");

        return producerService.sendTopicMessage(messageDTO,TOPIC_EXCHANGE);
    }

    /**
     * 根据组合id查询file
     * @param gwasAndLDFilenameDTOList
     * @return
     */
    private Map<String, List<String>> getListFilePathMap(List<GWASAndLDFilenameDTO> gwasAndLDFilenameDTOList) throws Exception{
        log.info("根据组合id查询file gwasAndLDFilenameDTOList="+JSON.toJSONString(gwasAndLDFilenameDTOList));
        Set<Object> idSet = new HashSet<>();
        List<String> ldNameList = new ArrayList<>();
        for (GWASAndLDFilenameDTO gwasAndLDFilenameDTO:gwasAndLDFilenameDTOList) {
            idSet.add(gwasAndLDFilenameDTO.getGwasFileId());
        }
        Map<String, Set<Object>> idMap = new HashMap<>();
        idMap.put("id",idSet);
        log.info("根据组合id查询file idMap="+JSON.toJSONString(idMap));
        List<PrsFile> fileList = fileService.getFileListByColumnMap(idMap);
        log.info("根据组合id查询file结束 fileList="+JSON.toJSONString(fileList));

        List<String> ldList = new ArrayList<>();
        List<String> gwasFilePathList = new ArrayList<>();
        for (GWASAndLDFilenameDTO gwasAndLDFilenameDTO:gwasAndLDFilenameDTOList) {
            Long gwasFileId = gwasAndLDFilenameDTO.getGwasFileId();
            Long ldFileId = gwasAndLDFilenameDTO.getLdFileId();

            for (PrsFile file : fileList) {
                if(gwasFileId.equals(file.getId())){
                    gwasFilePathList.add(file.getFilePath()+file.getFileName()+file.getFileSuffix());
                    ldList.add(LdEnum.valueOf(ldFileId.intValue()).getValue());
                }
            }

        }

        Map<String, List<String>> resMap = new HashMap<>();
        resMap.put("LD",ldList);
        resMap.put("GWAS",gwasFilePathList);

        log.info("根据组合id查询file结果 resMap="+JSON.toJSONString(resMap));
        return resMap;
    }

    /**
     * 根据多个id查询file
     * @param fileGWASId
     * @param fileLDId
     * @return
     */
    private Map<String, String> getFilePathMap(Long fileGWASId, Long fileLDId) throws Exception{
        log.info("根据多个id查询file fileGWASId="+fileGWASId+"\nfileLDId="+fileLDId);
        Map<String, String> resMap = new HashMap<>();

        Map<String, Set<Object>> idMap = new HashMap<>();
        Set<Object> idSet = new HashSet<>();
        idSet.add(fileGWASId);

        if(LdEnum.valueOf(fileLDId.intValue()) != null){
            //判断是否为固定值LD
            resMap.put("LD",LdEnum.valueOf(fileLDId.intValue()).getPath());
        }else{
            idSet.add(fileLDId);
        }
        idMap.put("id",idSet);

        log.info("根据多个id查询file idMap="+JSON.toJSONString(idMap));
        List<PrsFile> fileList = fileService.getFileListByColumnMap(idMap);
        log.info("根据多个id查询file结束 fileList="+JSON.toJSONString(fileList));

        for (PrsFile file:fileList) {
            String fileType = file.getFileType();
            if("GWAS".equals(fileType)){
                //页面上传GWAS文件完整地址
                resMap.put(fileType,"/srv"+file.getFilePath()+file.getFileName()+file.getFileSuffix());
            }else{
                String ldFilePath = "/srv"+file.getFilePath();
                //去掉最后一个目录分隔符
                resMap.put(fileType,((ldFilePath.length()-1 ==ldFilePath.lastIndexOf( File.separator)) ? ldFilePath.substring(0,ldFilePath.lastIndexOf( File.separator)) : ldFilePath) );
            }
        }

        log.info("根据多个id查询file resMap="+JSON.toJSONString(resMap));
        return resMap;
    }

    /**
     * 组装inputFile，填充parameter_enter表存储数据对象
     * @param algorithmReqDTO 页面传入参数
     * @param parameterEnters parameter_enter表存储数据封装对象
     * @param now 当前系统时间
     */
    private Map<String,Object> setInputParameterFile(AlgorithmReqDTO algorithmReqDTO,List<ParameterEnter> parameterEnters,LocalDateTime now,
                                                     Map<String,String> singleMap,
                                                     Map<String,List<String>> multipleMap) {
        log.info("组装inputFile，填充parameter_enter表存储数据对象入参：algorithmReqDTO="+JSON.toJSONString(algorithmReqDTO)
                +"\nparameterEnters="+JSON.toJSONString(parameterEnters)
                +"\nnow="+now.toString());

        HashMap<String,Object> resMap = new HashMap<>();


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

        //options模板地址
        resMap.put("optionsPath",algorithms.getOptionsPath());

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
            }else if(StringUtils.isNotEmpty(type) && type.indexOf("String(not an array)") != -1){
                //拼装json
                jsonObject.put(name+"."+parameterEnterReqDTO.getName()+"_1_value",parameterEnterReqDTO.getValue());
            }else{
                //拼装json
                jsonObject.put(name+"."+parameterEnterReqDTO.getName()+"_1_value",valArr);
            }

        }

        if(singleMap != null){
            jsonObject.put(name+"."+"summary_statistic_2",singleMap.get("GWAS"));
            jsonObject.put(name+"."+"ldref_0",singleMap.get("LD"));
        }else if(multipleMap != null){
            jsonObject.put(name+"."+"summary_statistic_2",multipleMap.get("GWAS"));
            jsonObject.put(name+"."+"pop_0",multipleMap.get("LD"));
        }

        resMap.put("inputJson",jsonObject);

        log.info("组装参数文件方法返回resMap="+JSON.toJSONString(resMap));
        return resMap;
    }

    /**
     * 调用工作流接口、存储runner_detail数据
     * @param resMap 组装上传参数文件返回结果
     * @param userId 用户id
     * @param now 当前系统时间
     * @param jobName
     * @return
     */
    private String submitWorkflow(Map<String,Object> resMap,String userId,LocalDateTime now,String jobName,
                                  Long fileGWASId,Long fileLDId, List<GWASAndLDFilenameDTO> gwasAndLDFilenameDTOList) {
//
//        log.info("调用工作流接口、存储runner_detail数据入参：resMap="+JSON.toJSONString(resMap)
//                +"\njobName="+jobName
//                +"\nnow="+now.toString());
//
//        File inputFile = new File((String) resMap.get("inputPath"));
//        File wdlFile = new File((String) resMap.get("wdlPath"));
//
//        Map<String,File> fileMap = new HashMap<>();
//        fileMap.put("workflowInputs",inputFile);
//        fileMap.put("workflowSource",wdlFile);
//
//        log.info("访问cromwell提交工作流");
//        String resultmsg = HttpClientUtil.httpClientUploadFileByfile(fileMap,cromwellUrl);
//        log.info("访问cromwell提交工作流返回结果"+JSON.toJSONString(resultmsg));
//        String cromwellId = null;
//        if(StringUtils.isNotEmpty(resultmsg)){
//            JSONObject  cromwellResult = JSON.parseObject(resultmsg);
//            cromwellId = cromwellResult.get("id").toString();
//            RunnerDetail runnerDetail = new RunnerDetail();
//            runnerDetail.setJobName(jobName);
//            runnerDetail.setWorkflowExecutionUuid(cromwellId);//工作流uuid
//            runnerDetail.setUserId(Long.valueOf(userId));
//            runnerDetail.setStatus(0);//运行状态 4:Finish, 3:Project at risk ,1:In progress,0:Not started
//            runnerDetail.setProgress(0);//运行进度 0-100
//            runnerDetail.setCreatedUser("system");
//            runnerDetail.setCreatedDate(now);
//            runnerDetail.setModifiedUser("system");
//            runnerDetail.setModifiedDate(now);
//            runnerDetail.setIsDelete(0);
//            log.info("保存工作流运行数据开始runnerDetail="+JSON.toJSONString(runnerDetail));
//            Boolean runnerFlag = runnerDetailBo.save(runnerDetail);
//            log.info("保存工作流运行数据结束runnerFlag="+runnerFlag);
//
//            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
//            runnerStatisReqDTO.setWorkflowExecutionUuid(cromwellId);
//            Long runnerId  = runnerDetailService.selectRunnerDetail(runnerStatisReqDTO).getId();
//
//            //保存file与工作流对应关系数据
//            if(CollectionUtils.isNotEmpty(gwasAndLDFilenameDTOList)){
//                List<RunnerDetailToFileReqDTO> runnerDetailToFileReqDTOList = new ArrayList<>();
//                for (GWASAndLDFilenameDTO gwasAndLDFilenameDTO:gwasAndLDFilenameDTOList) {
//                    RunnerDetailToFileReqDTO runnerDetailToFileReqDTO = new RunnerDetailToFileReqDTO();
//                    runnerDetailToFileReqDTO.setRunnerId(runnerId);
//                    runnerDetailToFileReqDTO.setLdFileId(gwasAndLDFilenameDTO.getLdFileId());
//                    runnerDetailToFileReqDTO.setGwasFileId(gwasAndLDFilenameDTO.getGwasFileId());
//                    runnerDetailToFileReqDTOList.add(runnerDetailToFileReqDTO);
//                }
//                log.info("保存file与工作流对应关系数据runnerDetailToFileReqDTOList="+JSON.toJSONString(runnerDetailToFileReqDTOList));
//                boolean muFlag = runnerDetailToFileService.saveBatch(runnerDetailToFileReqDTOList);
//                log.info("保存file与工作流对应关系数据结束muFlag="+muFlag);
//            }else{
//                RunnerDetailToFileReqDTO runnerDetailToFileReqDTO = new RunnerDetailToFileReqDTO();
//                runnerDetailToFileReqDTO.setLdFileId(fileLDId);
//                runnerDetailToFileReqDTO.setRunnerId(runnerId);
//                runnerDetailToFileReqDTO.setGwasFileId(fileGWASId);
//                log.info("保存file与工作流对应关系数据runnerDetailToFileReqDTO="+JSON.toJSONString(runnerDetailToFileReqDTO));
//                boolean singleFlag = runnerDetailToFileService.saveOrUpdate(runnerDetailToFileReqDTO);
//                log.info("保存file与工作流对应关系数据结束singleFlag="+singleFlag);
//            }
//        }
//        //删除本地临时文件
//        MultipartFileToFileUtil.delteTempFile(inputFile);
//        return cromwellId;
        return null;
    }

    /**
     * 存储runner_detail数据
     * @param userId
     * @param now
     * @param algorithmsReqDTO
     * @param messageId
     * @return
     */
    private Long saveRunnerDetail(String userId,LocalDateTime now,AlgorithmsReqDTO algorithmsReqDTO,String messageId) {

        log.info("调用存储runner_detail数据入参：algorithmsReqDTO="+JSONObject.toJSONString(algorithmsReqDTO)
                +"\nnow="+now.toString());
            RunnerDetail runnerDetail = new RunnerDetail();
            runnerDetail.setJobName(algorithmsReqDTO.getJobName());
            runnerDetail.setUserId(Long.valueOf(userId));
            runnerDetail.setMessageId(messageId);
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

            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setMessageId(messageId);
            Long runnerId  = runnerDetailService.selectRunnerDetail(runnerStatisReqDTO).getId();

            //保存file与工作流对应关系数据
            if(CollectionUtils.isNotEmpty(algorithmsReqDTO.getGwasAndLDFilenameDTOList())){
                List<RunnerDetailToFileReqDTO> runnerDetailToFileReqDTOList = new ArrayList<>();
                for (GWASAndLDFilenameDTO gwasAndLDFilenameDTO:algorithmsReqDTO.getGwasAndLDFilenameDTOList()) {
                    RunnerDetailToFileReqDTO runnerDetailToFileReqDTO = new RunnerDetailToFileReqDTO();
                    runnerDetailToFileReqDTO.setRunnerId(runnerId);
                    runnerDetailToFileReqDTO.setLdFileId(gwasAndLDFilenameDTO.getLdFileId());
                    runnerDetailToFileReqDTO.setGwasFileId(gwasAndLDFilenameDTO.getGwasFileId());
                    runnerDetailToFileReqDTOList.add(runnerDetailToFileReqDTO);
                }
                log.info("保存file与工作流对应关系数据runnerDetailToFileReqDTOList="+JSON.toJSONString(runnerDetailToFileReqDTOList));
                boolean muFlag = runnerDetailToFileService.saveBatch(runnerDetailToFileReqDTOList);
                log.info("保存file与工作流对应关系数据结束muFlag="+muFlag);
            }else{
                RunnerDetailToFileReqDTO runnerDetailToFileReqDTO = new RunnerDetailToFileReqDTO();
                runnerDetailToFileReqDTO.setLdFileId(algorithmsReqDTO.getFileLDId());
                runnerDetailToFileReqDTO.setRunnerId(runnerId);
                runnerDetailToFileReqDTO.setGwasFileId(algorithmsReqDTO.getFileGWASId());
                log.info("保存file与工作流对应关系数据runnerDetailToFileReqDTO="+JSON.toJSONString(runnerDetailToFileReqDTO));
                boolean singleFlag = runnerDetailToFileService.saveOrUpdate(runnerDetailToFileReqDTO);
                log.info("保存file与工作流对应关系数据结束singleFlag="+singleFlag);
            }
        return runnerId;
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
