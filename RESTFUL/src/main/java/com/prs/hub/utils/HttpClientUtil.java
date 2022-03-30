package com.prs.hub.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
public class HttpClientUtil {
    /**
     * 上传文件
     * @param filePathMap 上传的文件地址
     * @param url 上传的地址
     * @returnPath
     */
    public static String httpClientUploadFileByPath(Map<String, String> filePathMap,String url ) {
        log.info("上传文件filePathMap="+ JSON.toJSONString(filePathMap));
        log.info("上传地址url="+url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            for (String fileKey :filePathMap.keySet()) {
                String filePath = filePathMap.get(fileKey);
                File file = new File(filePath);
                FileInputStream fileInputStream = new FileInputStream(file);
                // MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
                // 其中originalFilename,String contentType 旧名字，类型  可为空
                // ContentType.APPLICATION_OCTET_STREAM.toString() 需要使用HttpClient的包
                MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);

                String fileName = multipartFile.getOriginalFilename();
                builder.addBinaryBody(fileKey, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            log.error("上传文件IOException",e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("上传文件Exception",e);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("关闭httpClient",e);
                e.printStackTrace();
            }
        }
        log.info("上传文件result="+result);
        return result;
    }
    /**
     * 上传文件
     * @param fileMap 上传的文件
     * @param url 上传的地址
     * @returnPath
     */
    public static String httpClientUploadFileByfile(Map<String, File> fileMap,String url ) {
        log.info("上传地址url="+url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            for (String fileKey :fileMap.keySet()) {
                File file = fileMap.get(fileKey);
                FileInputStream fileInputStream = new FileInputStream(file);
                // MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
                // 其中originalFilename,String contentType 旧名字，类型  可为空
                // ContentType.APPLICATION_OCTET_STREAM.toString() 需要使用HttpClient的包
                MultipartFile multipartFile = new MockMultipartFile("copy"+file.getName(),file.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);

                String fileName = multipartFile.getOriginalFilename();
                builder.addBinaryBody(fileKey, multipartFile.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            log.error("上传文件IOException",e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("上传文件Exception",e);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("关闭httpClient",e);
                e.printStackTrace();
            }
        }
        log.info("上传文件result="+result);
        return result;
    }
}
