package com.prs.hub.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.prs.hub.utils.MultipartFileToFileUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

                    LocalDateTime createdDate = prsFileRes.getCreatedDate();
                    if(createdDate!=null){
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        prsFileResDTO.setUploadDate(createdDate.format(df));
                        LocalDateTime deleteDate = createdDate.plusDays(30);
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
     * @param id
     * @param request
     * @param response
     */
    @Authorization
    @RequestMapping(value = "/download/{id}",method = RequestMethod.GET)
    public void downloadFiles(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response){
        OutputStream outputStream=null;
        InputStream inputStream=null;
        try {
            //先根据id查到文件信息
            PrsFile file = fileService.getFileById(id);
            String fileName = file.getFileName();
            //通过文件信息将文件转化为inputStream
            inputStream=fileService.getFileInputStream(file);
            //下载文件需要设置的header
            response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            // 获取输出流
            outputStream = response.getOutputStream();
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

}
