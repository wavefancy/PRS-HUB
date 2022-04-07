package com.prs.hub.file.controller;

import com.alibaba.fastjson.JSON;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.utils.MultipartFileToFileUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    /**
     * 文件上传接口
     */
    /*@Authorization
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public BaseResult upLoadFiles(@CurrentUser UserReqDTO userReqDTO,@RequestParam("file")MultipartFile multipartFile){
        log.info("文件上传Controller开始");
        log.info("userReqDTO="+ JSON.toJSON(userReqDTO));
        //如果文件为空，直接返回错误信息
        if (multipartFile.isEmpty()){
            return new BaseResult(ResultCodeEnum.FILE_EMPTY.getCode(),ResultCodeEnum.FILE_EMPTY.getName(),null);
        }
        //否则调用service上传文件
        return fileService.upLoadFiles(userReqDTO,multipartFile);
    }*/
    /**
     * sftp文件上传接口
     */
    @Authorization
    @RequestMapping(value = "/sftpupload",method = RequestMethod.POST)
    public BaseResult sftpUpLoadFiles(@CurrentUser UserReqDTO userReqDTO,@RequestParam("file")MultipartFile multipartFile){
        log.info("sftp文件上传接口Controller开始");
        log.info("userReqDTO="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        //如果文件为空，直接返回错误信息
        if (multipartFile.isEmpty()){
            return new BaseResult(ResultCodeEnum.FILE_EMPTY.getCode(),ResultCodeEnum.FILE_EMPTY.getName(),null);
        }
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
//            String filePath = userReqDTO.getEmail()+"/"+System.currentTimeMillis()+"/";
            Calendar c = Calendar.getInstance();
            String dataPath = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE);
            String filePath = dataPath+"/"+userReqDTO.getEmail()+"/"+fileName.substring(0,fileName.lastIndexOf("."))+System.currentTimeMillis()+"/";
            log.info("sftp文件上传controller,targetPath="+filePath+fileName);
            sftpSystemService.uploadFile(filePath+fileName,multipartFile.getInputStream());
            log.info("sftp文件上传成功");

            //将上传文件信息存储到数据库
            log.info("调用fileService将上传文件信息存储到数据库开始");
            Long fileId = fileService.saveFileDetail(filePath,fileName,userReqDTO);
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
        //先根据id查到文件信息
        PrsFile file = fileService.getFileById(id);
        String fileName = file.getFileName();
        //通过文件信息将文件转化为inputStream
        inputStream=fileService.getFileInputStream(file);
        //下载文件需要设置的header
        response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        // 获取输出流
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
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
