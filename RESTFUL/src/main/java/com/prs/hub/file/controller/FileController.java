package com.prs.hub.file.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.file.dto.PrsFileReqDTO;
import com.prs.hub.file.dto.PrsFileResDTO;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.runnerdetail.dto.RunnerStatisDTO;
import com.prs.hub.runnerdetail.dto.RunnerStatisReqDTO;
import com.prs.hub.statistics.service.StatisticsService;
import com.prs.hub.utils.StringUtils;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            PrsFile prsFile = new PrsFile();
            BeanUtils.copyProperties(prsFileReqDTO,prsFile);
            prsFile.setUserId(Long.valueOf(userReqDTO.getId()));
            List<PrsFile> prsFileList = fileService.getFileList(prsFile);

            List<PrsFileResDTO> resDTOList = new ArrayList<PrsFileResDTO>();
            if(CollectionUtils.isNotEmpty(prsFileList)){
                for (PrsFile prsFileRes:prsFileList) {
                    PrsFileResDTO prsFileResDTO = new PrsFileResDTO();
                    BeanUtils.copyProperties(prsFileRes,prsFileResDTO);

                    LocalDateTime modifiedDate = prsFileRes.getModifiedDate();
                    if(modifiedDate!=null){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        prsFileResDTO.setUploadDate(prsFileRes.getCreatedDate().format(df));
                        LocalDateTime deleteDate = modifiedDate.plusDays(30);
                        prsFileResDTO.setDeleteDate(deleteDate.format(df));
                        LocalDateTime now = LocalDateTime.now();
                        if(now.isAfter(deleteDate)&&!(now.format(df).equals(deleteDate.format(df)))){
                            prsFileResDTO.setStatus("expired");
                        }else{
                            prsFileResDTO.setStatus("validity");
                        }
                    }

                    resDTOList.add(prsFileResDTO);
                }
            }
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
    public BaseResult uploadFiles(@CurrentUser UserReqDTO userReqDTO,
                                  @RequestParam("file")MultipartFile multipartFile,
                                  HttpServletRequest req
    ){
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
    public void downloadResult(@RequestParam("uuid") String uuid, HttpServletRequest request, HttpServletResponse response){
        OutputStream outputStream=null;
        InputStream inputStream=null;
        try {
            RunnerStatisReqDTO runnerStatisReqDTO = new RunnerStatisReqDTO();
            runnerStatisReqDTO.setWorkflowExecutionUuid(uuid);
            List<RunnerStatisDTO> runnerStatisDTOList = statisticsService.getRunnerDetail(runnerStatisReqDTO);
            if(!CollectionUtils.isNotEmpty(runnerStatisDTOList)){
                return;
            }
            String path = runnerStatisDTOList.get(0).getResultPath();
            // 读到流中
            inputStream = new FileInputStream(path);// 文件的存放路径
            String fileName = new File(path).getName();
            response.reset();
            response.setContentType("application/octet-stream");
            //下载文件需要设置的header
            response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            // 获取输出流
            outputStream = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭流
            try {
                if (inputStream!=null){
                    inputStream.close();
                }
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
    public BaseResult deleteFile( @RequestParam("fileId") String fileId, HttpServletRequest request, HttpServletResponse response){
        log.info("删除文件fileId="+fileId);
        Map<String,Object> resultMap = new HashMap<>();
        if(StringUtils.isEmpty(fileId)){
            return new BaseResult(ResultCodeEnum.FILE_ID_EMPTY.getCode(),ResultCodeEnum.FILE_ID_EMPTY.getName(),null);
        }

        try {
            log.info("调用fileService删除文件开始fileId="+fileId);
            Boolean deleteRes = fileService.deleteByFileId(fileId);
            log.info("调用fileService删除文件结束deleteRes="+deleteRes);

            if(deleteRes){
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
                LocalDateTime modifiedDate = prsFileRes.getModifiedDate();

                LocalDateTime createdDate = prsFileRes.getCreatedDate();
                if(createdDate.format(df).equals(modifiedDate.format(df))){
                    PrsFileReqDTO prsFileUpdate = new PrsFileReqDTO();
                    prsFileUpdate.setId(prsFileRes.getId());
                    prsFileUpdate.setModifiedDate(createdDate.plusDays(30));
                    prsFileUpdate.setCreatedDate(createdDate);
                    log.info("调用fileService延长文件有效时间开始prsFileUpdate="+JSON.toJSONString(prsFileUpdate));
                    updateRes = fileService.updateFile(prsFileUpdate);
                    log.info("调用fileService延长文件有效时间结束updateRes="+updateRes);

                }

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
}
