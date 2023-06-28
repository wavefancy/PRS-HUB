package com.prs.hub.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.file.dto.FileChunkReqDTO;
import com.prs.hub.file.dto.PrsFileReqDTO;
import com.prs.hub.file.service.FileChunkService;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.bo.PrsFileBo;
import com.prs.hub.practice.entity.ParameterEnter;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.rabbitmq.dto.MQMessageDTO;
import com.prs.hub.rabbitmq.service.ProducerService;
import com.prs.hub.utils.CromwellUtil;
import com.prs.hub.utils.MultipartFileToFileUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@EnableScheduling
public class FileServiceImpl implements FileService {

    @Autowired
    private PrsFileBo fileBo;
    @Autowired
    private FileChunkService fileChunkService;
    /**
     * 发送消息
     */
    @Autowired
    private ProducerService producerService;

    @Value("${upload.exchange}")
    private String uploadExchange;

    @Value("${query.file.status.routing.key}")
    private String queryFileStatusRoutingKey;

    @Value("${delete.file.routing.key}")
    private String deleteFileRoutingKey;

    @Override
    public BaseResult upLoadFiles(String filePath,String fileName, MultipartFile file) {
        log.info("文件上传service开始filePath="+ filePath);
        log.info("文件上传service开始fileName="+ fileName);
        Map<String,Object> resultMap = new HashMap<>();


        File newFile=new File(filePath,fileName);

        //判断文件在所在目录是否存在，如果不存在就创建对应的目录
        if (!newFile.getParentFile().exists()){
            newFile.getParentFile().mkdirs();
        }

        try {
            //文件写入
            file.transferTo(newFile);

        } catch (Exception e) {
            log.error("文件上传异常",e);
            resultMap.put("code", ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","文件上传异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }

        resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
        resultMap.put("msg","文件上传成功");
        log.info("文件上传成功resultMap="+JSON.toJSON(resultMap));
        return BaseResult.ok("接口调用成功",resultMap);
    }

    @Override
    public PrsFile getFileById(String id) throws Exception {
        log.info("文件service根据文件id获取文件信息id="+id);
        PrsFile prsFile = fileBo.getById(id);
        log.info("文件service根据文件id获取文件信息prsFile="+JSON.toJSON(prsFile));
        return prsFile;
    }

    @Override
    public List<PrsFile> getFileList(PrsFile prsFile) throws Exception {
        log.info("文件service根据prsFile获取文件信息,prsFile="+JSON.toJSONString(prsFile));
        QueryWrapper<PrsFile> queryWrapper = new QueryWrapper<>();
        if(prsFile.getId()!=null){
            queryWrapper.eq("id",prsFile.getId());
        }
        if(prsFile.getUserId()!=null){
            queryWrapper.eq("user_id",prsFile.getUserId());
        }
        if(StringUtils.isNotEmpty(prsFile.getFileType())){
            queryWrapper.eq("file_type",prsFile.getFileType());
        }
        queryWrapper.orderByDesc("created_date");
        log.info("调用bo查询新增文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
        List<PrsFile> prsFileList = fileBo.list(queryWrapper);
//        IPage<PrsFile> iPage = new Page<>();
//        iPage.setCurrent(1);
//        iPage.setSize(3);
//        IPage<PrsFile> prsFileIPage = fileBo.page(iPage,queryWrapper);
//        log.info("prsFileIPage="+JSON.toJSON(prsFileIPage));
        log.info("调用bo查询新增文件的信息结束prsFileList="+JSON.toJSON(prsFileList));

        return prsFileList;
    }

    @Override
    public List<PrsFile> getFileListByColumnMap(Map<String, Set<Object>> columnMap) throws Exception {
        log.info("根据columnMap获取文件获取文件信息,prsFile="+JSON.toJSONString(columnMap));
        QueryWrapper<PrsFile> queryWrapper = new QueryWrapper<>();

        for (String key :columnMap.keySet()) {
            queryWrapper.in(key,columnMap.get(key));
        }
        queryWrapper.eq("is_delete",0);
        log.info("根据columnMap调用bo查询文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
        List<PrsFile> prsFileList = fileBo.list(queryWrapper);
        log.info("根据columnMap调用bo查询文件的信息结束prsFileList="+JSON.toJSON(prsFileList));

        return prsFileList;
    }

    @Override
    public IPage<PrsFile> getFileList(PrsFileReqDTO prsFileReqDTO) throws Exception {
        log.info("文件service根据prsFileReqDTO分页查询文件信息,prsFileReqDTO="+JSON.toJSONString(prsFileReqDTO));
        QueryWrapper<PrsFile> queryWrapper = new QueryWrapper<>();
        if(prsFileReqDTO.getId()!=null){
            queryWrapper.eq("id",prsFileReqDTO.getId());
        }
        if(prsFileReqDTO.getUserId()!=null){
            queryWrapper.eq("user_id",prsFileReqDTO.getUserId());
        }
        if(StringUtils.isNotEmpty(prsFileReqDTO.getFileType())){
            queryWrapper.eq("file_type",prsFileReqDTO.getFileType());
        }
        if(StringUtils.isNotEmpty(prsFileReqDTO.getParsingStatus())){
            queryWrapper.eq("parsing_status",prsFileReqDTO.getParsingStatus());
        }
        queryWrapper.orderByDesc("created_date");
        IPage<PrsFile> iPage = new Page<>();
        iPage.setCurrent(prsFileReqDTO.getCurrent());
        iPage.setSize(prsFileReqDTO.getSize());
        log.info("调用bo分页查询文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
        IPage<PrsFile> prsFileIPage = fileBo.page(iPage,queryWrapper);
        log.info("调用bo分页查询文件的信息结束prsFileIPage="+JSON.toJSON(prsFileIPage));

        return prsFileIPage;
    }
    @Override
    public InputStream getFileInputStream(PrsFile prsFile) {
        log.info("文件service将文件转化为InputStream");
        File file=new File(prsFile.getFilePath()+prsFile.getFileName()+prsFile.getFileSuffix());
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将这些文件的信息写入到数据库中
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param userReqDTO 用户信息
     * @return
     */
    @Override
    public Boolean saveOrUpdateFileDetail(String filePath,String fileName,UserReqDTO userReqDTO)throws Exception{
        log.info("文件的信息path="+filePath);
        log.info("文件的信息fileName="+fileName);
        log.info("用户信息userReqDTO="+JSON.toJSONString(userReqDTO));
        //获取到后缀名
        String suffixName = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : null;
        log.info("后缀名suffixName="+suffixName);

        //获取到后缀名
        String onlyName = fileName.contains(".") ? fileName.substring(0,fileName.lastIndexOf(".")) : fileName;
        log.info("上传文件名onlyName="+onlyName);

        Boolean flag = false;
        //将这些文件的信息写入到数据库中
        PrsFile prsFile = new PrsFile();
        prsFile.setFilePath(filePath);
        prsFile.setFileName(onlyName);
        prsFile.setFileSuffix(suffixName);
        prsFile.setUserId(Long.valueOf(userReqDTO.getId()));
        prsFile.setCreatedUser("system");
        prsFile.setCreatedDate(LocalDateTime.now());
        prsFile.setModifiedUser("system");
        prsFile.setModifiedDate(LocalDateTime.now());
        prsFile.setIsDelete(0);

        //更新条件
        UpdateWrapper<PrsFile> updateWrapper = new UpdateWrapper<PrsFile>();
        updateWrapper.eq("user_id",prsFile.getUserId());
        updateWrapper.eq("file_name",prsFile.getFileName());
        updateWrapper.eq("file_suffix",prsFile.getFileSuffix());

        log.info("将文件信息存储数据库开始prsFile="+JSON.toJSON(prsFile));
        flag = fileBo.saveOrUpdate(prsFile,updateWrapper);
        log.info("将文件信息存储数据库结束flag="+flag);
        return flag;
    }

    /**
     * 新增文件的信息
     * @param prsFile 文件信息
     * @return
     */
    @Override
    public Long saveFileDetail(PrsFile prsFile) throws Exception{
        log.info("新增文件的信息prsFile="+JSON.toJSONString(prsFile));

        Boolean flag = false;
        //将这些文件的信息写入到数据库中
        LocalDateTime now = LocalDateTime.now();
        prsFile.setCreatedUser("system");
        prsFile.setCreatedDate(now);
        prsFile.setModifiedUser("system");
        prsFile.setModifiedDate(now);
        prsFile.setDeleteDate(now.plusDays(30));
        prsFile.setIsDelete(0);

        log.info("调用bo新增文件的信息开始prsFile="+JSON.toJSON(prsFile));
        flag = fileBo.save(prsFile);
        log.info("调用bo新增文件的信息结束flag="+flag);

        Long resInt = null;
        if(flag){
            QueryWrapper<PrsFile> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",prsFile.getUserId());
            queryWrapper.eq("file_path",prsFile.getFilePath());
            queryWrapper.eq("file_name",prsFile.getFileName());
            queryWrapper.eq("file_suffix",prsFile.getFileSuffix());
            log.info("调用bo查询新增文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
            PrsFile prsFileRes = fileBo.getOne(queryWrapper);
            log.info("调用bo查询新增文件的信息结束prsFileRes="+JSON.toJSONString(prsFileRes));
            if(prsFileRes != null){
                resInt = prsFileRes.getId();
            }
        }
        return resInt;
    }

    /**
     * 根据id删除文件
     * @param fileId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean deleteByFileId(String fileId) throws Exception {
        log.info("物理删除文件fileId="+fileId);

        //查询要删除的文件信息
        log.info("根据fileId="+fileId+"查询要删除的文件信息");
        PrsFile prsFileRes = fileBo.getById(fileId);
        log.info("查询要删除的文件信息返回prsFileRes="+prsFileRes);

        if(prsFileRes == null){
            log.info("该文件不存在");
            return true;
        }

        //删除文件和表数据
        Boolean removeRes =  deletePrsFile(prsFileRes);

        return removeRes;
    }

    @Override
    public Boolean updateFile(PrsFileReqDTO prsFileReqDTO) throws Exception {
        log.info("修改文件prsFileReqDTO="+JSON.toJSONString(prsFileReqDTO));
        PrsFile prsFile = new PrsFile();
        prsFile.setId(prsFileReqDTO.getId());
        if(StringUtils.isNotEmpty(prsFileReqDTO.getFilePath())){
            prsFile.setFilePath(prsFileReqDTO.getFilePath());
        }
        if(StringUtils.isNotEmpty(prsFileReqDTO.getFilePath())){
            prsFile.setFileName(prsFileReqDTO.getFileName());
        }
        if(StringUtils.isNotEmpty(prsFileReqDTO.getFileSuffix())){
            prsFile.setFileSuffix(prsFileReqDTO.getFileSuffix());
        }
        if(StringUtils.isNotEmpty(prsFileReqDTO.getParsingId())){
            prsFile.setParsingId(prsFileReqDTO.getParsingId());
        }
        if(StringUtils.isNotEmpty(prsFileReqDTO.getParsingStatus())){
            prsFile.setParsingStatus(prsFileReqDTO.getParsingStatus());
        }
        if(prsFileReqDTO.getUserId()!=null){
            prsFile.setUserId(prsFileReqDTO.getUserId());
        }
        if(prsFileReqDTO.getModifiedDate()!=null){
            prsFile.setModifiedDate(prsFileReqDTO.getModifiedDate());
        }
        if(prsFileReqDTO.getCreatedDate()!=null){
            prsFile.setCreatedDate(prsFileReqDTO.getCreatedDate());
        }
        if(prsFileReqDTO.getDeleteDate()!=null){
            prsFile.setDeleteDate(prsFileReqDTO.getDeleteDate());
        }

        //更新条件
        UpdateWrapper<PrsFile> updateWrapper = new UpdateWrapper<PrsFile>();
        updateWrapper.eq("id",prsFileReqDTO.getId());
        log.info("修改文件prsFile="+JSON.toJSONString(prsFile));
        Boolean updateRes =  fileBo.update(prsFile,updateWrapper);

        log.info("修改文件updateRes="+updateRes);
        return updateRes;
    }

    @Override
    public Boolean sendDeleteFilesMsg(List<String> filePathList){
        log.info("删除文件发送消息入参：{}", JSONObject.toJSONString(filePathList));
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("filePathList",filePathList);
        String messageId = UUID.randomUUID().toString();

        JSONObject inputJson = new JSONObject(reqMap);

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(deleteFileRoutingKey);
        messageDTO.setTag(deleteFileRoutingKey);

        return producerService.sendTopicMessage(messageDTO,uploadExchange);
    }

    /**
     * 定时任务：每天0点5秒时清理过期的file表数据
     */
    @Scheduled(cron = "5 0 0 * * ?")
    private void  deleteExpiredFile(){
        log.info("每天0点5秒时清理过期的file表数据");

        QueryWrapper<PrsFile> queryWrapper = new QueryWrapper();
        queryWrapper.eq("is_delete",0);

        LocalDateTime now = LocalDateTime.now();
        queryWrapper.between("delete_date",now,now.plusDays(1));
        log.info("查询到期日在今天范围内的file数据");
        List<PrsFile> prsFileList = fileBo.list(queryWrapper);
        log.info("查询到期日在今天范围内的file数据返回prsFileList="+JSON.toJSONString(prsFileList));
        if(CollectionUtils.isNotEmpty(prsFileList)){
            for (PrsFile prsFile : prsFileList) {
                //删除文件
                deletePrsFile(prsFile);
                //删除分片记录
                String identifier = prsFile.getIdentifier();
                if(StringUtils.isNotEmpty(identifier)){
                    FileChunkReqDTO fileChunkReqDTO = new FileChunkReqDTO();
                    fileChunkReqDTO.setIdentifier(identifier);
                    log.info("调用fileChunkService删除文件开始identifier="+identifier);
                    Boolean deleteFileChunk = fileChunkService.deleteFileChunk(fileChunkReqDTO);
                    log.info("调用fileChunkService删除文件结束deleteFileChunk="+deleteFileChunk);
                }
            }
        }
    }
    /**
     * 定时任务：每30分钟检查file表ParsingStatus数据，纯在‘N’类型的结果则发送消息查询工作流状态若成功则变更为'Y'
     */
    @Scheduled(cron = "0 0/30 * * * *")
    private void  updateParsingStatus(){
        log.info("定时任务：每30分钟检查file表ParsingStatus数据，纯在‘N’类型的结果则查询工作流状态若成功则变更为'Y'");
        QueryWrapper<PrsFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parsing_status","N");
        queryWrapper.eq("file_type","LD");
        queryWrapper.isNotNull("parsing_id");//解析工作流id
        IPage<PrsFile> iPage = new Page<>();
        log.info("调用bo查询ParsingStatus数据，纯在‘N’类型的结果开始queryWrapper="+JSON.toJSON(queryWrapper));
        List<PrsFile> prsFileList = fileBo.list(queryWrapper);
        log.info("调用bo查询ParsingStatus数据，纯在‘N’类型的结果结束prsFileList="+JSON.toJSON(prsFileList));

        if(CollectionUtils.isNotEmpty(prsFileList)){
            StringBuffer strB = new StringBuffer();
            for (PrsFile prsFile:prsFileList) {
                if(strB.length() == 0){
                    strB.append(prsFile.getParsingId());
                }else{
                    strB.append(","+prsFile.getParsingId());
                }
            }

            //发送查询状态消息
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("uuidArr",strB.toString());
            this.sendQueryFileStatusMessage(reqMap);
        }

    }
    /**
     * 物理删除文件
     * @param prsFile
     * @return
     */
    @Override
    public  Boolean deletePrsFile(PrsFile prsFile){
        //文件路径
        String fileFullPath = prsFile.getFilePath();
        log.info("要删除的文件全路径fileFullPath="+fileFullPath);

        //物理删除文件
        File delteFile = new File(fileFullPath);
        MultipartFileToFileUtil.delteTempFile(delteFile);

        List<String> filePathList = new ArrayList<>();
        filePathList.add(fileFullPath);
        this.sendDeleteFilesMsg(filePathList);

        log.info("物理删除文件记录开始prsFile="+JSON.toJSONString(prsFile));
        Boolean removeRes =  fileBo.removeById(prsFile);
        log.info("物理删除文件记录结束removeRes="+removeRes);

        return removeRes;
    }

    /**
     * 发送消息
     * @param reqMap
     */
    private Boolean sendQueryFileStatusMessage(Map<String, Object> reqMap) {
        log.info("文件上传保存数据后调用发送消息入参：{}", JSONObject.toJSONString(reqMap));

        String messageId = UUID.randomUUID().toString();

        JSONObject inputJson = new JSONObject(reqMap);

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(queryFileStatusRoutingKey);
        messageDTO.setTag(queryFileStatusRoutingKey);

        return producerService.sendTopicMessage(messageDTO,uploadExchange);
    }
}
