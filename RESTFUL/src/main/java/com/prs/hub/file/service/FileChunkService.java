package com.prs.hub.file.service;

import com.prs.hub.file.dto.FileChunkReqDTO;
import com.prs.hub.file.dto.FileChunkResDTO;

import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/8/9 10:23
 */
public interface FileChunkService {
    /**
     * 根据文件 md5md5标识 查询
     *
     * @param param
     * @return
     */
    List<FileChunkResDTO> list(FileChunkReqDTO param);

    /**
     * 保存记录
     *
     * @param param 记录参数
     */
    void saveFileChunk(FileChunkReqDTO param);
    /**
     * 删除分片记录
     * @param param
     * @return
     */
    Boolean deleteFileChunk(FileChunkReqDTO param);
}
