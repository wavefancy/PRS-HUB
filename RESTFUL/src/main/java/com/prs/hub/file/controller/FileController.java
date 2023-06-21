package com.prs.hub.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.algorithms.dto.AlgorithmsReqDTO;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.file.dto.FileChunkReqDTO;
import com.prs.hub.file.dto.PrsFileReqDTO;
import com.prs.hub.file.dto.PrsFileResDTO;
import com.prs.hub.file.service.FileChunkService;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.entity.FileChunk;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.rabbitmq.dto.MQMessageDTO;
import com.prs.hub.rabbitmq.service.ProducerService;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.statistics.service.StatisticsService;
import com.prs.hub.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping(value = "/prs/hub")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private SFTPSystemService sftpSystemService;
    @Value("${file.max-size}")
    private String maxSize;
    @Value("${upload.file.path}")
    private String uploadFilePath;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private FileChunkService fileChunkService;

    /**
     * 发送消息
     */
    @Autowired
    private ProducerService producerService;

    @Value("${upload.exchange}")
    private String uploadExchange;

    @Value("${upload.file.routing.key}")
    private String uploadRoutingKey;


    /**
     * 获取文件信息
     */
    @Authorization
    @RequestMapping(value = "/getFileList",method = RequestMethod.GET)
    public BaseResult getFileList(@CurrentUser UserReqDTO userReqDTO, HttpServletRequest req, PrsFileReqDTO prsFileReqDTO){
        log.info("获取文件信息Controller开始prsFileReqDTO="+JSON.toJSONString(prsFileReqDTO));
        log.info("userReqDTO="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        try {
//            PrsFile prsFile = new PrsFile();
//            BeanUtils.copyProperties(prsFileReqDTO,prsFile);
//            prsFile.setUserId(Long.valueOf(userReqDTO.getId()));
//            List<PrsFile> prsFileList = fileService.getFileList(prsFile);
            prsFileReqDTO.setUserId(Long.valueOf(userReqDTO.getId()));
            log.info("调用service分页查询文件信息开始prsFileReqDTO="+JSON.toJSONString(prsFileReqDTO));
            IPage<PrsFile> prsFileIPage = fileService.getFileList(prsFileReqDTO);
            log.info("调用service分页查询文件信息结束prsFileIPage="+JSON.toJSONString(prsFileIPage));
            Long total = prsFileIPage.getTotal();
            List<PrsFileResDTO> resDTOList = new ArrayList<PrsFileResDTO>();
            if(total != 0){
                for (PrsFile prsFileRes:prsFileIPage.getRecords()) {
                    PrsFileResDTO prsFileResDTO = new PrsFileResDTO();
                    BeanUtils.copyProperties(prsFileRes,prsFileResDTO);
                    String fileType = prsFileResDTO.getFileType();
                    String parsingStatus = prsFileResDTO.getParsingStatus();//ld文件解析状态

                    LocalDateTime modifiedDate = prsFileRes.getModifiedDate();
                    LocalDateTime createdDate = prsFileRes.getCreatedDate();
                    LocalDateTime deleteDate = prsFileRes.getDeleteDate();
                    if(modifiedDate!=null){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        prsFileResDTO.setUploadDate(createdDate.format(df));
                        //检查是否延长过有效期
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime checkDate = now.plusDays(30);
                        prsFileResDTO.setDeleteDate(deleteDate.format(df));
                        if("LD".equals(fileType) && "N".equals(parsingStatus)){
                            prsFileResDTO.setStatus("unconverted ");//未转换
                        }else if(now.isAfter(deleteDate)&&!(now.format(df).equals(deleteDate.format(df)))){
                            prsFileResDTO.setStatus("expired");//失效
                        }else if(checkDate.format(df).equals(deleteDate.format(df))){
                            prsFileResDTO.setStatus("refreshed");//已延期
                        }else{
                            prsFileResDTO.setStatus("validity");
                        }
                    }

                    resDTOList.add(prsFileResDTO);
                }
            }
            resultMap.put("total",prsFileIPage.getTotal());
            resultMap.put("current",prsFileIPage.getCurrent());
            resultMap.put("resDTOList",resDTOList);
            resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("msg","获取文件信息成功");
        }catch (Exception e){
            log.error("获取文件信息controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","获取文件信息异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }

    /**
     * 文件上传接口
     * @param userReqDTO
     * @param multipartFile
     * @param req
     * @return
     */
    @Authorization
    @RequestMapping(value = "/uploadFiles",method = RequestMethod.POST)
    public BaseResult uploadFiles(@CurrentUser UserReqDTO userReqDTO, @RequestParam("file")MultipartFile multipartFile,  HttpServletRequest req ){
        log.info("文件上传接口Controller开始fileType="+req.getParameter("fileType"));
        log.info("userReqDTO="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        //如果文件为空，直接返回错误信息
        if (multipartFile.isEmpty()){
            return new BaseResult(ResultCodeEnum.FILE_EMPTY.getCode(),ResultCodeEnum.FILE_EMPTY.getName(),null);
        }
        String fileNameReq = req.getParameter("fileName");
        String fileType = req.getParameter("fileType");
        String descrition = req.getParameter("descrition");
        //设置支持最大上传的文件，这里是1024*1024*20=20M
        long MAX_SIZE=Long.valueOf(maxSize);
        //获取要上传文件的名称
        String fileName=multipartFile.getOriginalFilename();
        log.info("上传文件的名称fileName="+fileName);
        //如果名称为空，返回一个文件名为空的错误
        if (StringUtils.isEmpty(fileName)){
            log.info(ResultCodeEnum.FILE_NAME_EMPTY.getName()+"fileName="+fileName);
            resultMap.put("code", ResultCodeEnum.FILE_NAME_EMPTY.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_NAME_EMPTY.getName());
            return BaseResult.ok("文件上传接口调用成功",resultMap);
        }
        //如果文件超过最大值，返回超出可上传最大值的错误
        if (multipartFile.getSize()>MAX_SIZE){
            log.info(ResultCodeEnum.FILE_MAX_SIZE.getName()+"fileSize="+multipartFile.getSize());
            resultMap.put("code", ResultCodeEnum.FILE_MAX_SIZE.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_MAX_SIZE.getName());
            return BaseResult.ok("文件上传接口调用成功",resultMap);
        }
        try {
            //上传文件到服务器
            //获取到后缀名
            String suffixName = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : null;
            log.info("后缀名suffixName="+suffixName);
            //获取到文件名
            String onlyName = null;
            if(StringUtils.isNotEmpty(fileNameReq)){//当页面传入自定义文件名时
                onlyName = fileNameReq;
            }else{
                onlyName = fileName.contains(".") ? fileName.substring(0,fileName.lastIndexOf(".")) : fileName;
            }
            log.info("上传文件名onlyName="+onlyName);
//            String filePath = userReqDTO.getEmail()+"/"+System.currentTimeMillis()+"/";
            Calendar c = Calendar.getInstance();
            String dataPath = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE);
            String filePath = uploadFilePath+dataPath+Calendar.getInstance().getTimeInMillis()+"/"+fileType+"/"+userReqDTO.getEmail()+"/"+onlyName+"/";
            log.info("文件上传controller,targetPath="+filePath+onlyName+suffixName);
//            sftpSystemService.uploadFile(filePath+onlyName+suffixName,multipartFile.getInputStream());
            fileService.upLoadFiles(filePath,onlyName+suffixName,multipartFile);
            log.info("文件上传成功");

            //将上传文件信息存储到数据库
            PrsFile prsFile = new PrsFile();

            prsFile.setDescrition(descrition);
            prsFile.setFileType(fileType);
            prsFile.setFilePath(filePath);
            prsFile.setFileName(onlyName);
            prsFile.setFileSuffix(suffixName);
            prsFile.setUserId(Long.valueOf(userReqDTO.getId()));
            log.info("调用fileService将上传文件信息存储到数据库开始");
            Long fileId = fileService.saveFileDetail(prsFile);
            log.info("调用fileService将上传文件信息存储到数据库结束fileId="+fileId);
            if(fileId !=null){
                resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
                resultMap.put("msg","sftp文件上传成功");
                resultMap.put("fileId",fileId);
            }else {
                resultMap.put("code",ResultCodeEnum.FAIL.getCode());
                resultMap.put("msg","sftp文件上传失败");
            }


        }catch (Exception e){
            log.error("文件上传controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","文件上传异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }

    /**
     * 保存上传文件信息
     * @param userReqDTO
     * @param req
     * @return
     */
    @Authorization
    @RequestMapping(value = "/savePrsFileInfo",method = RequestMethod.POST)
    public BaseResult savePrsFileInfo(@CurrentUser UserReqDTO userReqDTO,  HttpServletRequest req ){
        Map<String,Object> resultMap = new HashMap<>();
        //文件名
        String fileName = req.getParameter("fileName");
        //文件描述
        String descrition = req.getParameter("descrition");
        //文件类型
        String fileType = req.getParameter("fileType");
        //存储路径
        String filePath = req.getParameter("filePath");
        //文件后缀
        String suffixName = req.getParameter("suffixName");
        //文件分片存储标识
        String identifier = req.getParameter("identifier");
        //人种信息
        String pop = req.getParameter("pop");

        log.info("保存上传文件信息controller:\nfileName="+fileName+"\ndescrition="+descrition+"\nfileType="+fileType+"\nfilePath="+filePath+"\nsuffixName="+suffixName);
        if(StringUtils.isEmpty(fileName) || StringUtils.isEmpty(fileType) || StringUtils.isEmpty(filePath)){
            resultMap.put("code", ResultCodeEnum.FILE_NAME_EMPTY.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_NAME_EMPTY.getName());
            return BaseResult.ok("文件上传接口调用成功",resultMap);
        }

        try {
            //将上传文件信息存储到数据库
            PrsFile prsFile = new PrsFile();
            if("LD".equals(fileType)){
                prsFile.setParsingStatus("N");//N代表未解析完成
            }

            prsFile.setDescrition(descrition);
            prsFile.setFileType(fileType);
            prsFile.setFilePath(filePath);
            prsFile.setFileName(fileName);
            prsFile.setFileSuffix(suffixName);
            prsFile.setIdentifier(identifier);
            prsFile.setUserId(Long.valueOf(userReqDTO.getId()));
            log.info("调用fileService将上传文件信息存储到数据库开始");
            Long fileId = fileService.saveFileDetail(prsFile);
            log.info("调用fileService将上传文件信息存储到数据库结束fileId="+fileId);
            resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
            resultMap.put("msg","sftp文件上传成功");
            resultMap.put("fileId",fileId);

            if(fileId !=null && "LD".equals(fileType)){
                //发送消息对LD文件进行解析
                Map<String,Object> uploadMsgReq = new HashMap<>();
                uploadMsgReq.put("identifier",identifier);
                uploadMsgReq.put("fileId",fileId);
                uploadMsgReq.put("fileType",fileType);
                uploadMsgReq.put("filePath",filePath);
                uploadMsgReq.put("fileName",fileName);
                uploadMsgReq.put("suffixName",suffixName);
                uploadMsgReq.put("userId",userReqDTO.getId());
                uploadMsgReq.put("pop",pop);
                this.sendUploadMessage(uploadMsgReq);
            }

        }catch (Exception e){
            log.error("文件上传controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","文件上传异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);

    }
    /**
     * sftp文件上传接口
     */
    @Authorization
    @RequestMapping(value = "/sftpupload",method = RequestMethod.POST)
    public BaseResult sftpUpLoadFiles(@CurrentUser UserReqDTO userReqDTO,
                                      @RequestParam("file")MultipartFile multipartFile,
                                        HttpServletRequest req
                                      ){
        log.info("sftp文件上传接口Controller开始fileType="+req.getParameter("fileType"));
        log.info("userReqDTO="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        //如果文件为空，直接返回错误信息
        if (multipartFile.isEmpty()){
            return new BaseResult(ResultCodeEnum.FILE_EMPTY.getCode(),ResultCodeEnum.FILE_EMPTY.getName(),null);
        }
        String fileNameReq = req.getParameter("fileName");
        String fileType = req.getParameter("fileType");
        String descrition = req.getParameter("descrition");
        //设置支持最大上传的文件，这里是1024*1024*20=20M
        long MAX_SIZE=Long.valueOf(maxSize);
        //获取要上传文件的名称
        String fileName=multipartFile.getOriginalFilename();
        log.info("上传文件的名称fileName="+fileName);
        //如果名称为空，返回一个文件名为空的错误
        if (StringUtils.isEmpty(fileName)){
            log.info(ResultCodeEnum.FILE_NAME_EMPTY.getName()+"fileName="+fileName);
            resultMap.put("code", ResultCodeEnum.FILE_NAME_EMPTY.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_NAME_EMPTY.getName());
            return BaseResult.ok("接口调用成功",resultMap);
        }
        //如果文件超过最大值，返回超出可上传最大值的错误
        if (multipartFile.getSize()>MAX_SIZE){
            log.info(ResultCodeEnum.FILE_MAX_SIZE.getName()+"fileSize="+multipartFile.getSize());
            resultMap.put("code", ResultCodeEnum.FILE_MAX_SIZE.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_MAX_SIZE.getName());
            return BaseResult.ok("接口调用成功",resultMap);
        }
        try {
            //上传文件到服务器
            //获取到后缀名
            String suffixName = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : null;
            log.info("后缀名suffixName="+suffixName);
            //获取到文件名
            String onlyName = null;
            if(StringUtils.isNotEmpty(fileNameReq)){//当页面传入自定义文件名时
                onlyName = fileNameReq;
            }else{
                onlyName = fileName.contains(".") ? fileName.substring(0,fileName.lastIndexOf(".")) : fileName;
            }
            log.info("上传文件名onlyName="+onlyName);
//            String filePath = userReqDTO.getEmail()+"/"+System.currentTimeMillis()+"/";
            Calendar c = Calendar.getInstance();
            String dataPath = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE);
            String filePath = dataPath+"/"+fileType+"/"+userReqDTO.getEmail()+"/"+onlyName+"/";
            log.info("sftp文件上传controller,targetPath="+filePath+fileName);
            sftpSystemService.uploadFile(filePath+onlyName+suffixName,multipartFile.getInputStream());
            log.info("sftp文件上传成功");

            //将上传文件信息存储到数据库
            PrsFile prsFile = new PrsFile();

            prsFile.setDescrition(descrition);
            prsFile.setFileType(fileType);
            prsFile.setFilePath(filePath);
            prsFile.setFileName(onlyName);
            prsFile.setFileSuffix(suffixName);
            prsFile.setUserId(Long.valueOf(userReqDTO.getId()));
            log.info("调用fileService将上传文件信息存储到数据库开始");
            Long fileId = fileService.saveFileDetail(prsFile);
            log.info("调用fileService将上传文件信息存储到数据库结束fileId="+fileId);
            if(fileId !=null){
                resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
                resultMap.put("msg","sftp文件上传成功");
                resultMap.put("fileId",fileId);
            }else {
                resultMap.put("code",ResultCodeEnum.FAIL.getCode());
                resultMap.put("msg","sftp文件上传失败");
            }


        }catch (Exception e){
            log.error("sftp文件上传controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","sftp文件上传异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }
    /**
     * 文件下载接口
     * @param request
     * @param response
     */
    @Authorization
    @RequestMapping(value = "/downloadResult",method = RequestMethod.GET)
    public void downloadResult(@RequestParam("uuid") String uuid,@RequestParam("status") String status, HttpServletRequest request, HttpServletResponse response){
        log.info("下载结果文件：uuid="+uuid);
        OutputStream outputStream=null;
        try {
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setWorkflowExecutionUuid(uuid);
            List<RunnerStatisDTO> runnerStatisDTOList = statisticsService.getRunnerDetail(runnerStatisReqDTO);
            if(!CollectionUtils.isNotEmpty(runnerStatisDTOList)){
                return;
            }
            String folderPath = runnerStatisDTOList.get(0).getResultPath();
            if("Failed".equals(status)){
                //将结果地址替换为日志输出地址
                folderPath = folderPath.replace("outputs","wf_logs");
            }
//            // 读到流中
//            inputStream = new FileInputStream(path);// 文件的存放路径
//            String fileName = new File(path).getName();
//            response.reset();
//            response.setContentType("application/octet-stream");
//            //下载文件需要设置的header
//            response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
//            // 获取输出流
//            outputStream = response.getOutputStream();
//            byte[] b = new byte[1024];
//            int len;
//            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
//            while ((len = inputStream.read(b)) > 0) {
//                outputStream.write(b, 0, len);
//            }
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"download.zip\"");
            outputStream = response.getOutputStream() ;
            FolderDownloaderUtils.downloadZipFolder(folderPath, outputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭流
            try {
                if (outputStream!=null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 删除文件
     * @param fileId
     * @param request
     * @param response
     */
    @Authorization
    @RequestMapping(value = "/deleteFile",method = RequestMethod.GET)
    public BaseResult deleteFile( @RequestParam("fileId") String fileId,@RequestParam(value = "identifier", required=false ) String identifier ,HttpServletRequest request, HttpServletResponse response){
        log.info("删除文件fileId="+fileId);
        log.info("删除文件identifier="+identifier);
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(fileId)){
            log.info("必要参数fileId为空"+fileId);
            return new BaseResult(ResultCodeEnum.FILE_ID_EMPTY.getCode(),ResultCodeEnum.FILE_ID_EMPTY.getName(),null);
        }

        try {
            log.info("调用fileService删除文件开始fileId="+fileId);
            Boolean deleteRes = fileService.deleteByFileId(fileId);
            log.info("调用fileService删除文件结束deleteRes="+deleteRes);

            if(deleteRes){
                //删除分片记录
                FileChunkReqDTO fileChunkReqDTO = new FileChunkReqDTO();
                fileChunkReqDTO.setIdentifier(identifier);
                log.info("调用fileChunkService删除文件开始identifier="+identifier);
                Boolean deleteFileChunk = fileChunkService.deleteFileChunk(fileChunkReqDTO);
                log.info("调用fileChunkService删除文件结束deleteFileChunk="+deleteFileChunk);

                resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
                resultMap.put("msg","删除文件成功");
            }else {
                resultMap.put("code",ResultCodeEnum.FAIL.getCode());
                resultMap.put("msg","删除文件失败");
            }

        }catch (Exception e){
            log.error("删除文件controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","删除文件");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }
    /**
     * 延长文件有效时间
     * @param fileId
     * @param request
     * @param response
     */
    @Authorization
    @RequestMapping(value = "/extensionFileValidTime",method = RequestMethod.GET)
    public BaseResult updateFile( @RequestParam("fileId") String fileId, HttpServletRequest request, HttpServletResponse response){
        log.info("controller延长文件有效时间fileId="+fileId);
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(fileId)){
            return new BaseResult(ResultCodeEnum.FILE_ID_EMPTY.getCode(),ResultCodeEnum.FILE_ID_EMPTY.getName(),null);
        }

        try {
            log.info("调用fileService获取文件信息开始fileId="+fileId);
            PrsFile prsFileRes = fileService.getFileById(fileId);
            log.info("调用fileService获取文件信息结束prsFileList="+JSON.toJSONString(prsFileRes));

            Boolean updateRes = false;
            if(prsFileRes != null){
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime deleteDate = prsFileRes.getDeleteDate();
                LocalDateTime nowDate =  LocalDateTime.now();

                /**
                 * 已经在当前的时间点上延长了30天
                 */
                if(nowDate.plusDays(30).format(df).equals(deleteDate.format(df))){//没有延期过的才进行延期修改
                    log.info("已经在当前的时间点上延长了30天");
                    resultMap.put("code",ResultCodeEnum.FAIL.getCode());
                    resultMap.put("msg","已经在当前的时间点上延长了30天");
                    return BaseResult.ok("接口调用成功",resultMap);
                }

                PrsFileReqDTO prsFileUpdate = new PrsFileReqDTO();
                prsFileUpdate.setId(prsFileRes.getId());
                prsFileUpdate.setModifiedDate(LocalDateTime.now());
                prsFileUpdate.setDeleteDate(nowDate.plusDays(30));
                log.info("调用fileService延长文件有效时间开始prsFileUpdate="+JSON.toJSONString(prsFileUpdate));
                updateRes = fileService.updateFile(prsFileUpdate);
                log.info("调用fileService延长文件有效时间结束updateRes="+updateRes);
            }
            if(updateRes){
                resultMap.put("code",ResultCodeEnum.SUCCESS.getCode());
                resultMap.put("msg","延长文件有效时间成功");
            }else {
                resultMap.put("code",ResultCodeEnum.FAIL.getCode());
                resultMap.put("msg","延长文件有效时间失败");
            }
        }catch (Exception e){
            log.error("延长文件有效时间controller异常",e);
            resultMap.put("code",ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","延长文件有效时间异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }
        return BaseResult.ok("接口调用成功",resultMap);
    }

    /**
     * 发送消息
     * @param resMap
     */
    private Boolean sendUploadMessage(Map<String, Object> resMap) {
        log.info("文件上传保存数据后调用发送消息入参：{}",JSONObject.toJSONString(resMap));

        String messageId = UUID.randomUUID().toString();

        JSONObject inputJson = new JSONObject(resMap);

        MQMessageDTO messageDTO = new MQMessageDTO();
        messageDTO.setMessage(inputJson.toJSONString());
        messageDTO.setMsgId(messageId);
        messageDTO.setRoutingKey(uploadRoutingKey);
        messageDTO.setTag(uploadRoutingKey);

        return producerService.sendTopicMessage(messageDTO,uploadExchange);
    }

    public static void downloadFolder(String folderPath, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            Path folder = Paths.get(folderPath);
            if (!Files.exists(folder) || !Files.isDirectory(folder)) {
                throw new FileNotFoundException("Folder not found: " + folderPath);
            }

            Files.walk(folder)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            String relativePath = folder.relativize(path).toString();
                            zipOutputStream.putNextEntry(new ZipEntry(relativePath));
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
