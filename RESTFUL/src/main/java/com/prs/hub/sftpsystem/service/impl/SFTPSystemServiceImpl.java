package com.prs.hub.sftpsystem.service.impl;

import com.jcraft.jsch.*;
import com.prs.hub.sftpsystem.dto.SFTPPropertiesDTO;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;

@Slf4j
@Service
public class SFTPSystemServiceImpl implements SFTPSystemService {

    @Autowired
    private SFTPPropertiesDTO config;

    // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
    private static final String SESSION_CONFIG_STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    /**
     * 创建SFTP连接
     * @return
     * @throws Exception
     */
    private ChannelSftp createSftp() throws Exception {
        JSch jsch = new JSch();
        log.info("尝试连接 sftp[" + config.getUsername() + "@" + config.getHost() + "], 使用密码[" + config.getPassword() + "]");

        Session session = createSession(jsch, config.getHost(), config.getUsername(), config.getPort());
        session.setPassword(config.getPassword());
        session.connect(config.getSessionConnectTimeout());

        log.info("会话连接到 {}.", config.getHost());

        Channel channel = session.openChannel(config.getProtocol());
        channel.connect(config.getChannelConnectedTimeout());

        log.info("通道创建 {}.", config.getHost());

        return (ChannelSftp) channel;
    }

    /**
     * 加密秘钥方式登陆
     * @return
     */
    private ChannelSftp connectByKey() throws Exception {
        JSch jsch = new JSch();

        // 设置密钥和密码 ,支持密钥的方式登陆
        if (StringUtils.isNotBlank(config.getPrivateKey())) {
            if (StringUtils.isNotBlank(config.getPassphrase())) {
                // 设置带口令的密钥
                jsch.addIdentity(config.getPrivateKey(), config.getPassphrase());
            } else {
                // 设置不带口令的密钥
                jsch.addIdentity(config.getPrivateKey());
            }
        }
        log.info("Try to connect sftp[" + config.getUsername() + "@" + config.getHost() + "], use private key[" + config.getPrivateKey()
                + "] with passphrase[" + config.getPassphrase() + "]");

        Session session = createSession(jsch, config.getHost(), config.getUsername(), config.getPort());
        // 设置登陆超时时间
        session.connect(config.getSessionConnectTimeout());
        log.info("Session connected to " + config.getHost() + ".");

        // 创建sftp通信通道
        Channel channel = session.openChannel(config.getProtocol());
        channel.connect(config.getChannelConnectedTimeout());
        log.info("Channel created to " + config.getHost() + ".");
        return (ChannelSftp) channel;
    }

    /**
     * 上传文件
     * @param targetPath
     * @param inputStream
     * @return
     * @throws Exception
     */
    @Override
    public boolean uploadFile(String targetPath, InputStream inputStream) throws Exception {
        ChannelSftp sftp = this.createSftp();
        try {
            sftp.cd(config.getRoot());
            log.info("改变路径到 {}", config.getRoot());

            int index = targetPath.lastIndexOf("/");
            String fileDir = targetPath.substring(0, index);
            String fileName = targetPath.substring(index + 1);
            boolean dirs = this.createDirs(fileDir, sftp);
            if (!dirs) {
                log.error("目标路径错误. targetPath:{}", targetPath);
                throw new Exception("sftpservice上传文件失败");
            }
            sftp.put(inputStream, fileName);
            return true;
        } catch (Exception e) {
            log.error("sftpservice上传文件异常. TargetPath: {}", targetPath, e);
            throw new Exception("sftpservice上传文件失败");
        } finally {
            this.disconnect(sftp);
            if(inputStream != null ){
                inputStream.close();
            }
        }
    }

    /**
     * 上传文件
     * @param targetPath
     * @param file
     * @return
     * @throws Exception
     */
    @Override
    public boolean uploadFile(String targetPath, File file) throws Exception {
        return this.uploadFile(targetPath, new FileInputStream(file));
    }

    /**
     * 下载文件
     * @param targetPath
     * @return
     * @throws Exception
     */
    @Override
    public File downloadFile(String targetPath) throws Exception {
        ChannelSftp sftp = this.createSftp();
        OutputStream outputStream = null;
        try {
            sftp.cd(config.getRoot());
            log.info("改变路径到 {}", config.getRoot());

            File file = new File(targetPath.substring(targetPath.lastIndexOf("/") + 1));

            outputStream = new FileOutputStream(file);
            sftp.get(targetPath, outputStream);
            log.info("sftp下载文件成功. TargetPath: {}", targetPath);
            return file;
        } catch (Exception e) {
            log.error("sftp下载文件异常. TargetPath: {}", targetPath, e);
            throw new Exception("sftp下载文件失败");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            this.disconnect(sftp);
        }
    }
    /**
     * 根据路径获取文件
     * @param targetPath
     * @return
     * @throws Exception
     */
    @Override
    public File toFileByPath(String targetPath) throws Exception {
        ChannelSftp sftp = this.createSftp();
        OutputStream outputStream = null;
        try {
            sftp.cd(config.getRoot());
            log.info("改变路径到 {}", config.getRoot());

            File file = new File(targetPath.substring(targetPath.lastIndexOf("/") + 1));

            return file;
        } catch (Exception e) {
            log.error("sftp根据路径获取文件异常. TargetPath: {}", targetPath, e);
            throw new Exception("sftp根据路径获取文件失败");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            this.disconnect(sftp);
        }
    }

    /**
     * 删除文件
     * @param targetPath
     * @return
     * @throws Exception
     */
    @Override
    public boolean deleteFile(String targetPath) throws Exception {
        ChannelSftp sftp = null;
        try {
            sftp = this.createSftp();
            sftp.cd(config.getRoot());
            sftp.rm(targetPath);
            return true;
        } catch (Exception e) {
            log.error("删除文件异常. TargetPath: {}", targetPath, e);
            throw new Exception("删除文件失败");
        } finally {
            this.disconnect(sftp);
        }
    }

    /**
     * 创建一级或者多级目录
     * @param dirPath
     * @param sftp
     * @return
     */
    private boolean createDirs(String dirPath, ChannelSftp sftp) {
        if (dirPath != null && !dirPath.isEmpty()
                && sftp != null) {
            String[] dirs = Arrays.stream(dirPath.split("/"))
                    .filter(StringUtils::isNotBlank)
                    .toArray(String[]::new);

            for (String dir : dirs) {
                try {
                    sftp.cd(dir);
                    log.info("切换目录到 {}", dir);
                } catch (Exception e) {
                    try {
                        sftp.mkdir(dir);
                        log.info("创建目录成功 {}", dir);
                    } catch (SftpException e1) {
                        log.error("创建目录异常, directory:{}", dir, e1);
                        e1.printStackTrace();
                    }
                    try {
                        sftp.cd(dir);
                        log.info("切换目录到 {}", dir);
                    } catch (SftpException e1) {
                        log.error("切换目录异常, directory:{}", dir, e1);
                        e1.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 创建session
     * @param jsch
     * @param host
     * @param username
     * @param port
     * @return
     * @throws Exception
     */
    private Session createSession(JSch jsch, String host, String username, Integer port) throws Exception {
        Session session = null;

        if (port <= 0) {
            session = jsch.getSession(username, host);
        } else {
            session = jsch.getSession(username, host, port);
        }

        if (session == null) {
            throw new Exception(host + " session is null");
        }

        session.setConfig(SESSION_CONFIG_STRICT_HOST_KEY_CHECKING, config.getSessionStrictHostKeyChecking());
        return session;
    }

    /**
     * 关闭连接
     * @param sftp
     */
    private void disconnect(ChannelSftp sftp) {
        try {
            if (sftp != null) {
                if (sftp.isConnected()) {
                    sftp.disconnect();
                } else if (sftp.isClosed()) {
                    log.info("sftp 已经关闭了");
                }
                if (null != sftp.getSession()) {
                    sftp.getSession().disconnect();
                }
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
}
