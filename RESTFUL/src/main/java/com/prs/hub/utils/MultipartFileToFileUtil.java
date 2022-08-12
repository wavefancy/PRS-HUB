package com.prs.hub.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author fanshupeng
 * @create 2022/3/24 13:50
 */
@Slf4j
public class MultipartFileToFileUtil {
    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (Exception e) {
            log.error("获取流文件异常",e);
        }finally {
            try {
                if(os != null){
                    os.close();
                    log.info("已关闭os流");
                }
                if(ins != null){
                    ins.close();
                    log.info("已关闭ins流");
                }
            }catch (Exception e){
                log.error("流关闭异常",e);
            }
        }
    }

    /**
     * 删除本地临时文件
     *
     * @param file
     */
    public static Boolean delteTempFile(File file) {
        Boolean flag = false;

        if (file != null) {
            flag = file.delete();
        }
        log.info("删除本地临时文件flag="+flag);

        return flag;
    }
}

