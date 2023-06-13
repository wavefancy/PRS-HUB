package com.prs.hub.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author fanshupeng
 * @create 2023/4/14 17:00
 */
@Slf4j
@Component
public class ProcessUtils {

    /**
     * 源文件地址
     */
    @Value("${rsync.source.host}")
    private  String sourceHost;

    /**
     * 源服务器用户名
     */
    @Value("${rsync.source.user}")
    private  String sourceUser;

    /**
     * 源服务器密码
     */
    @Value("${rsync.source.password}")
    private  String sourcePassword;

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

        // 构建rsync命令

        String rsyncCommand =commandPull +" "+sourceUser + "@"+sourceHost+":" +  source +" "+ destinationPath + "/" +destinationFileName;

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
     * @param source
     * @param destinationFileName
     * @return
     */
    public  boolean rsyncPush(String source,String destinationFileName){
        log.info("rsync推送拷贝数据源：{} \n 拷贝目的地地址：{}" , source,destinationPath+"/"+destinationFileName);

        boolean res = false;

        // 构建rsync命令

        String rsyncCommand =commandPull+" "+ destinationPath + "/" +destinationFileName +" "+sourceUser + "@"+sourceHost+":" +  source ;

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
}
