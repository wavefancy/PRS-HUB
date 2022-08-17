package com.prs.hub.file.service;

import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.file.dto.PrsFileReqDTO;
import com.prs.hub.practice.entity.PrsFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileService {
    /**
     * 文件上传
     * @param filePath
     * @param fileName
     * @param file
     * @return
     */
    BaseResult upLoadFiles(String filePath,String fileName, MultipartFile file);
    /**
     * 根据id获取文件
     * @param id
     * @return
     */
    PrsFile getFileById(String id) throws Exception;
    /**
     * 根据prsFile获取文件
     * @param prsFile
     * @return
     */
    List<PrsFile> getFileList(PrsFile prsFile) throws Exception;
    /**
     * 根据file获取数据流
     * @param prsFile
     * @return
     */
    InputStream getFileInputStream(PrsFile prsFile);

    /**
     * 新增或修改文件的信息
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param userReqDTO 用户信息
     * @return
     */
    Boolean saveOrUpdateFileDetail(String filePath,String fileName,UserReqDTO userReqDTO) throws Exception;
    /**
     * 新增文件的信息
     * @param prsFile 文件信息
     * @return
     */
    Long saveFileDetail(PrsFile prsFile) throws Exception;

    /**
     * 删除文件
     * @param fileId
     * @return
     * @throws Exception
     */
    Boolean deleteByFileId(String fileId)throws Exception;
    /**
     * 物理删除文件
     * @param prsFile
     * @return
     */
    Boolean deletePrsFile(PrsFile prsFile);

    /**
     * 修改文件信息
     * @param prsFileReqDTO
     * @return
     * @throws Exception
     */
    Boolean updateFile(PrsFileReqDTO  prsFileReqDTO)throws Exception;
}
