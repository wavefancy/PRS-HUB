package com.prs.hub.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author fanshupeng
 * @create 2023/4/14 17:00
 */
@Slf4j
@Component
public class ProcessUtils {

    /**
     * 远程文件地址
     */
    @Value("${rsync.remote.host}")
    private  String remoteHost;

    /**
     * 远程服务器用户名
     */
    @Value("${rsync.remote.user}")
    private  String remoteUser;

    /**
     * 远程服务器密码
     */
    @Value("${rsync.remote.password}")
    private  String remotePassword;

    /**
     * 目标地址
     */
    @Value("${rsync.destination.path}")
    private  String destinationPath;

    /**
     * 执行命令参数
     */
    @Value("${rsync.command.pull}")
    private  String commandPull;

    /**
     * 远程拉取数据
     * @param source
     * @param destinationFileName
     * @return
     */
    public  boolean rsyncPull(String source,String destinationFileName){
        log.info("rsync拉取拷贝数据源：{} \n 拷贝目的地地址：{}" , source,destinationPath+"/"+destinationFileName);

        boolean res = false;
        String destinationFilePath = destinationPath+ File.separator + destinationFileName.substring(0,destinationFileName.lastIndexOf( File.separator));
        log.info("如果目录不存在则创建目录：{}",destinationFilePath);
        createDirectory(destinationFilePath);

        // 构建rsync命令

        String rsyncCommand =commandPull +" "+remoteUser + "@"+remoteHost+":" +  source +" "+ destinationPath + "/" +destinationFileName;

        // 执行rsync命令
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash", "-c", rsyncCommand);
        try {
            log.info("rsync执行拉取命令：{}", rsyncCommand);

            Process process = builder.start();
            int exitValue  = process.waitFor();

            if (exitValue == 0) {
                res = true;
                log.info("rsync拉取拷贝成功res:{}",res);
            } else {
                log.info("rsync拉取拷贝失败exitValue:{}",exitValue);
            }
        } catch (IOException e) {
            log.error("rsync拉取拷贝IOException:{}",e.getMessage());
            return res;
        } catch (InterruptedException e) {
            log.error("rsync拉取拷贝InterruptedException:{}",e.getMessage());
            return res;
        }

        return res;
    }

    /**
     * 远程推送数据
     * @param remote
     * @param destinationFileName
     * @return
     */
    public  boolean rsyncPush(String remote,String destinationFileName){
        log.info("rsync推送拷贝数据源：{} \n 拷贝目的地地址：{}" , remote,destinationPath+"/"+destinationFileName);

        boolean res = false;

        // 构建rsync命令

        String rsyncCommand =commandPull+" " +destinationFileName +" "+remoteUser + "@"+remoteHost+":" +  remote ;

        // 执行rsync命令
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("bash", "-c", rsyncCommand);
        try {
            log.info("rsync执行推送命令：{}", rsyncCommand);

            Process process = builder.start();
            int exitValue  = process.waitFor();

            if (exitValue == 0) {
                res = true;
                log.info("rsync推送拷贝成功res:{}",res);
            } else {
                log.info("rsync推送拷贝失败exitValue:{}",exitValue);
            }
        } catch (IOException e) {
            log.error("rsync推送拷贝IOException:{}",e.getMessage());
            return res;
        } catch (InterruptedException e) {
            log.error("rsync推送拷贝InterruptedException:{}",e.getMessage());
            return res;
        }

        return res;
    }
    public void createDirectory(String path) {
        File directory = new File(path);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("目录已创建成功");
            } else {
                log.info("无法创建目录");
            }
        } else {
            log.info("目录已存在");
        }
    }
}
