package com.prs.hub.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.file.dto.FileChunkReqDTO;
import com.prs.hub.file.dto.FileChunkResDTO;
import com.prs.hub.file.service.FileChunkService;
import com.prs.hub.practice.bo.FileChunkBo;
import com.prs.hub.practice.entity.FileChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/8/9 11:15
 */
@Slf4j
@Service
public class FileChunkServiceImpl implements FileChunkService {

    @Autowired
    private FileChunkBo fileChunkBo;

    @Override
    public List<FileChunkResDTO> list(FileChunkReqDTO param) {
        log.info("根据:"+ JSONObject.toJSONString(param) +"查询开始");

        QueryWrapper<FileChunk> fileChunkQueryWrapper = new QueryWrapper<>();
        fileChunkQueryWrapper.eq("identifier",param.getIdentifier());
        fileChunkQueryWrapper.eq("user_id",param.getUserId());

        log.info("查询FileChunk数据库开始");
        List<FileChunk> fileChunkList = fileChunkBo.list(fileChunkQueryWrapper);
        log.info("查询FileChunk数据库结束fileChunkList="+ JSON.toJSONString(fileChunkList));

        List<FileChunkResDTO> fileChunkResDTOList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(fileChunkList)){
            //组装返回数据
            for (FileChunk fileChunk : fileChunkList ) {
                FileChunkResDTO fileChunkResDTO = new FileChunkResDTO();
                BeanUtils.copyProperties(fileChunk,fileChunkResDTO);
                fileChunkResDTOList.add(fileChunkResDTO);
            }
        }
        log.info("根据:"+ JSONObject.toJSONString(param) +"查询结束："+JSON.toJSONString(fileChunkResDTOList));
        return fileChunkResDTOList;
    }

    /**
     * 保存分片记录
     * @param param 记录参数
     */
    @Override
    public void saveFileChunk(FileChunkReqDTO param) {
        Long id = param.getId();
        log.info("保存分片记录service开始id="+id);
        log.info("保存分片记录service开始md5id="+param.getIdentifier());
        FileChunk fileChunk= null;
        if (!param.isNew()) {
            fileChunk = fileChunkBo.getById(id);
            if (null == fileChunk) {
                throw new RuntimeException("id="+id+"数据为空");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (null == fileChunk) {
            fileChunk = new FileChunk();
        } else {
            fileChunk.setUpdateTime(now);
        }
        BeanUtils.copyProperties(param, fileChunk, "id");
        fileChunk.setFileName(param.getFilename());
        Boolean result;
        if (param.isNew()) {
            fileChunk.setAddTime(now);
            fileChunk.setUpdateTime(now);
            result = fileChunkBo.save(fileChunk);
            log.info("保存分片数据结果result="+result);
        } else {
            UpdateWrapper<FileChunk> fileChunkUpdateWrapper = new UpdateWrapper<>();
            fileChunkUpdateWrapper.eq("id",id);
            result = fileChunkBo.saveOrUpdate(fileChunk,fileChunkUpdateWrapper);
            log.info("修改分片数据结果result="+result);
        }

        if (!result) {
            throw new RuntimeException("保存数据失败");
        }

    }

    /**
     * 删除分片记录
     * @param param
     * @return
     */
    @Override
    public Boolean deleteFileChunk(FileChunkReqDTO param){
        log.info("删除分片记录identifier="+param.getIdentifier());

        Boolean deleteFlag = false;
        QueryWrapper<FileChunk> fileChunkQueryWrapper = new QueryWrapper<>();
        fileChunkQueryWrapper.eq("identifier",param.getIdentifier());

        deleteFlag = fileChunkBo.remove(fileChunkQueryWrapper);
        log.info("删除分片记录identifier结果deleteFlag="+deleteFlag);

        return deleteFlag;
    }
}
