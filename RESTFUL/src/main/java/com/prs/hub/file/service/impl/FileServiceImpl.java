package com.prs.hub.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.bo.PrsFileBo;
import com.prs.hub.practice.entity.PrsFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private PrsFileBo fileBo;

   /* @Override
    public BaseResult upLoadFiles(UserReqDTO userReqDTO,MultipartFile file) {
        log.info("文件上传service开始file="+ JSON.toJSON(userReqDTO));
        Map<String,Object> resultMap = new HashMap<>();
        //设置支持最大上传的文件，这里是1024*1024*2=2M
        long MAX_SIZE=2097152L;
        //获取要上传文件的名称
        String fileName=file.getOriginalFilename();
        log.info("上传文件的名称fileName="+fileName);
        //如果名称为空，返回一个文件名为空的错误
        if (StringUtils.isEmpty(fileName)){
            log.info(ResultCodeEnum.FILE_NAME_EMPTY.getName()+"fileName="+fileName);
            resultMap.put("code", ResultCodeEnum.FILE_NAME_EMPTY.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_NAME_EMPTY.getName());
            return BaseResult.ok("接口调用成功",resultMap);
        }
        //如果文件超过最大值，返回超出可上传最大值的错误
        if (file.getSize()>MAX_SIZE){
            log.info(ResultCodeEnum.FILE_MAX_SIZE.getName()+"fileSize="+file.getSize());
            resultMap.put("code", ResultCodeEnum.FILE_MAX_SIZE.getCode());
            resultMap.put("msg",ResultCodeEnum.FILE_MAX_SIZE.getName());
            return BaseResult.ok("接口调用成功",resultMap);
        }

        //上传文件路径
        String filePath = savePath+File.separator+userReqDTO.getEmail();
        log.info("上传文件路径filePath="+filePath);


        //文件的保存重新按照时间戳命名
//        String newName = System.currentTimeMillis() + suffixName;

        File newFile=new File(filePath,fileName);

        //判断文件在所在目录是否存在，如果不存在就创建对应的目录
        if (!newFile.getParentFile().exists()){
            newFile.getParentFile().mkdirs();
        }

        //落库标志
        Boolean flag = false;
        try {
            //文件写入
            file.transferTo(newFile);
            flag = saveOrUpdateFileDetail(filePath,fileName,userReqDTO);
        } catch (Exception e) {
            log.error("文件上传异常",e);
            resultMap.put("code", ResultCodeEnum.EXCEPTION.getCode());
            resultMap.put("msg","文件上传异常");
            return BaseResult.ok("接口调用成功",resultMap);
        }

        if(!flag){
            log.error("文件上传数据落库失败");
            resultMap.put("code", ResultCodeEnum.FAIL.getCode());
            resultMap.put("msg","文件上传数据落库失败");
            return BaseResult.ok("接口调用成功",resultMap);
        }

        resultMap.put("code", ResultCodeEnum.SUCCESS.getCode());
        resultMap.put("msg","文件上传成功");
        log.info("文件上传成功resultMap="+JSON.toJSON(resultMap));
        return BaseResult.ok("接口调用成功",resultMap);
    }*/

    @Override
    public PrsFile getFileById(String id) {
        log.info("文件service根据文件id获取文件信息id="+id);
        PrsFile prsFile = fileBo.getById(id);
        log.info("文件service根据文件id获取文件信息prsFile="+JSON.toJSON(prsFile));
        return prsFile;
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
    public Boolean saveOrUpdateFileDetail(String filePath,String fileName,UserReqDTO userReqDTO){
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
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param userReqDTO 用户信息
     * @return
     */
    @Override
    public Long saveFileDetail(String filePath,String fileName,UserReqDTO userReqDTO){
        log.info("新增文件的信息path="+filePath);
        log.info("新增文件的信息fileName="+fileName);
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
}
