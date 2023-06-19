package com.prs.hub.utils;

import com.prs.hub.config.COSConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fanshupeng
 * @create 2023/6/15 11:33
 */
@Slf4j
@Component
public class TencentCOSUtils {
    //默认线程池大小
    private static final int DEFAULT_THREAD_POOL_SIZE = 5;
    /**
     * 获取配置信息
     */
    @Autowired
    private  COSConfig cosConfig;

    /**
     * 获取临时密钥
     * @return
     */
    private Response  getCredential(){
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        Response response = null;
        try {
           // 您的云 api 密钥 SecretId
            config.put("secretId", cosConfig.getSecretId());
            // 您的云 api 密钥 SecretKey
            config.put("secretKey", cosConfig.getSecretKey());

            // 设置域名:
            // 如果您使用了腾讯云 cvm，可以设置内部域名
            //config.put("host", "sts.internal.tencentcloudapi.com");

            // 临时密钥有效时长，单位是秒，默认 1800 秒，目前主账号最长 2 小时（即 7200 秒），子账号最长 36 小时（即 129600）秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", cosConfig.getBucketName());
            // 换成 bucket 所在地区
            config.put("region", cosConfig.getRegionName());

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径
            // 列举几种典型的前缀授权场景：
            // 1、允许访问所有对象："*"
            // 2、允许访问指定的对象："a/a1.txt", "b/b1.txt"
            // 3、允许访问指定前缀的对象："a*", "a/*", "b/*"
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefixes", new String[] {
                    "*"
            });

            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分块上传需要以下的权限，其他权限列表请参见 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分块上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload",
                    //下载
                    "name/cos:GetObject"
            };
            config.put("allowActions", allowActions);


            response = CosStsClient.getCredential(config);
            log.info("临时密钥SecretId:",response.credentials.tmpSecretId);
            log.info("临时密钥SecretKey:",response.credentials.tmpSecretKey);
            log.info("临时密钥sessionToken:",response.credentials.sessionToken);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }
        return response;
    }

    /**
     * 生成 cos 客户端
     * @return
     */
    private COSClient getCOSClient(){
        Response response = this.getCredential();
        BasicSessionCredentials cred = new BasicSessionCredentials(response.credentials.tmpSecretId, response.credentials.tmpSecretKey, response.credentials.sessionToken);
        // 1 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分
        //COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-beijing
        Region region = new Region(cosConfig.getRegionName());
        ClientConfig clientConfig = new ClientConfig(region);
        // 2 生成 cos 客户端
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    /**
     * 分片上传cos
     * @param key  文件在COS中的存储路径和名称
     * @param filePath 本地文件路径
     */
    public void uploadLargeFile(String key, String filePath) {
        log.info("分片上传cos入参key:{}\nfilePath:{}",key,filePath);
        File file = new File(filePath);
        long contentLength = file.length();

        COSClient cosClient = this.getCOSClient();
        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(cosConfig.getBucketName(), key);
        InitiateMultipartUploadResult initResult = cosClient.initiateMultipartUpload(initRequest);
        String uploadId = initResult.getUploadId();

        long partSize = 20 * 1024 * 1024; // 20MB
        long filePosition = 0;

        try {
            while (filePosition < contentLength) {
                long partLength = Math.min(partSize, contentLength - filePosition);

                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(cosConfig.getBucketName())
                        .withKey(key)
                        .withUploadId(uploadId)
                        .withPartNumber((int) (filePosition / partSize) + 1)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partLength);

                executorService.execute(() -> {
                    try {
                        UploadPartResult uploadPartResult = cosClient.uploadPart(uploadRequest);
                        log.info("分片上传cos的Part#" + uploadPartResult.getPartNumber() + " 完成。");
                    } catch (CosClientException e) {
                        log.error("分片上传cos CosClientException异常:{}",e.getMessage());
                    }
                });
                filePosition += partSize;
            }
        }catch (Exception e){
            log.error("分片上传cos异常:{}",e.getMessage());
        }finally {
            executorService.shutdown();
            cosClient.shutdown();
        }

        CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(cosConfig.getBucketName(), key, uploadId, null);
        CompleteMultipartUploadResult completeMultipartUploadResult = cosClient.completeMultipartUpload(completeRequest);
        log.error("上传完成");
    }

}
