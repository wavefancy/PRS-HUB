package com.prs.hub.sftpsystem.service;

import java.io.File;
import java.io.InputStream;

/**
 * @Auth fsp
 */
public interface SFTPSystemService {
    boolean uploadFile(String targetPath, InputStream inputStream) throws Exception;

    boolean uploadFile(String targetPath, File file) throws Exception;

    File downloadFile(String targetPath) throws Exception;
    /**
     * 根据路径获取文件
     * @param targetPath
     * @return
     * @throws Exception
     */
    File toFileByPath(String targetPath) throws Exception;
    boolean deleteFile(String targetPath) throws Exception;

}
