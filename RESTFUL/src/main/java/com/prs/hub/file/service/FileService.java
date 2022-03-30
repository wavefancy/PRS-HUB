package com.prs.hub.file.service;

import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.practice.entity.PrsFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {
    /**
     * 文件上传
     * @param multipartFile
     * @return
     */
//    BaseResult upLoadFiles(UserReqDTO userReqDTO, MultipartFile multipartFile);
    /**
     * 根据id获取文件
     * @param id
     * @return
     */
    PrsFile getFileById(String id);
    /**
     * 根据file获取数据流
     * @param prsFile
     * @return
     */
    InputStream getFileInputStream(PrsFile prsFile);

    /**
     * 将文件的信息写入到数据库中
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param userReqDTO 用户信息
     * @return
     */
    Boolean saveOrUpdateFileDetail(String filePath,String fileName,UserReqDTO userReqDTO);

}
