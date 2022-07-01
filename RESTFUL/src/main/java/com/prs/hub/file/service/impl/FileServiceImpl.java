package com.prs.hub.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.constant.ResultCodeEnum;
import com.prs.hub.file.dto.PrsFileReqDTO;
import com.prs.hub.file.service.FileService;
import com.prs.hub.practice.bo.PrsFileBo;
import com.prs.hub.practice.entity.ParameterEnter;
import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private PrsFileBo fileBo;

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
        log.info("调用bo查询新增文件的信息开始queryWrapper="+JSON.toJSON(queryWrapper));
        List<PrsFile> prsFileList = fileBo.list(queryWrapper);
        log.info("调用bo查询新增文件的信息结束prsFileList="+JSON.toJSON(prsFileList));
        return prsFileList;
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

    @Override
    public Boolean deleteByFileId(String fileId) throws Exception {
        log.info("物理删除文件fileId="+fileId);
        PrsFile prsFile = new PrsFile();
        prsFile.setId(Long.valueOf(fileId));
        Boolean removeRes =  fileBo.removeById(prsFile);
        log.info("物理删除文件removeRes="+removeRes);
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
        if(prsFileReqDTO.getUserId()!=null){
            prsFile.setUserId(prsFileReqDTO.getUserId());
        }
        if(prsFileReqDTO.getModifiedDate()!=null){
            prsFile.setModifiedDate(prsFileReqDTO.getModifiedDate());
        }
        if(prsFileReqDTO.getCreatedDate()!=null){
            prsFile.setCreatedDate(prsFileReqDTO.getCreatedDate());
        }

        //更新条件
        UpdateWrapper<PrsFile> updateWrapper = new UpdateWrapper<PrsFile>();
        updateWrapper.eq("id",prsFileReqDTO.getId());
        log.info("修改文件prsFile="+JSON.toJSONString(prsFile));
        Boolean updateRes =  fileBo.update(prsFile,updateWrapper);

        log.info("修改文件updateRes="+updateRes);
        return updateRes;
    }
}
