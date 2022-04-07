package com.prs.hub;

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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class Test {

    public static void main(String[] args) throws ParseException {

//        final String remote_url = "http://39.103.140.193:8000/api/workflows/v1";// 第三方服务器请求地址
//        try {
//            Map<String, String> filePathMap =  new HashMap<String,String>();
//            filePathMap.put("workflowSource", "E:\\test\\hello.wdl");
//            filePathMap.put("workflowInputs","E:\\test\\inputs.json");
//            httpClientUploadFile(filePathMap,remote_url);
//
//        }catch (Exception e){
//            log.error("",e);
//        }
        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime());

        // 获得年份
        System.out.println("现在是：" + c.get(Calendar.YEAR) + "年");

        // 获得月份
        System.out.println("现在是：" + (c.get(Calendar.MONTH)+1) + "月");

        //获得日期
        System.out.println("现在是：" + c.get(Calendar.DATE) + "号");
        System.out.println("现在是：" + c.get(Calendar.DAY_OF_MONTH) + "号");

        // 获得这是今年的第几天
        System.out.println("现在是今年第" + c.get(Calendar.DAY_OF_YEAR) + "天");

        // 获得今天周几
        System.out.println("现在是星期：" + (c.get(Calendar.DAY_OF_WEEK)-1) );

        // 获得今天是这个月的第几周
        System.out.println("现在是第：" + c.get(Calendar.DAY_OF_WEEK_IN_MONTH) + "周" );

        // 12小时制的时间
        System.out.println("现在是：" + c.get(Calendar.HOUR) + "点");

        // 24小时制的时间
        System.out.println("现在是：" + c.get(Calendar.HOUR_OF_DAY) + "点");

        // 分钟数
        System.out.println("现在是：" + c.get(Calendar.MINUTE) + "分");

        // 秒数
        System.out.println("现在是：" + c.get(Calendar.SECOND) + "秒");

        // 毫秒
        System.out.println("现在是：" + c.get(Calendar.MILLISECOND) + "毫秒");







        SimpleDateFormat dc = new SimpleDateFormat();
        dc.applyPattern("yyyy-MM-dd");
        Date d = dc.parse("2017-5-13");
        c.setTime(d);

        System.out.println("--------------------2017-5-13信息-----------------------");

        // 获得年份
        System.out.println("现在是：" + c.get(Calendar.YEAR) + "年");

        // 获得月份
        System.out.println("现在是：" + (c.get(Calendar.MONTH)+1) + "月");

        //获得日期
        System.out.println("现在是：" + c.get(Calendar.DATE) + "号");
        System.out.println("现在是：" + c.get(Calendar.DAY_OF_MONTH) + "号");

        // 获得这是今年的第几天
        System.out.println("现在是今年第" + c.get(Calendar.DAY_OF_YEAR) + "天");

        // 获得今天周几
        System.out.println("现在是星期：" + (c.get(Calendar.DAY_OF_WEEK)-1) );

        // 获得今天是这个月的第几周
        System.out.println("现在是第：" + c.get(Calendar.DAY_OF_WEEK_IN_MONTH) + "周" );
    }

    /**
     * 上传文件
     * @param filePathMap 上传的文件地址
     * @param url 上传的地址
     * @returnPath
     */
    public static String httpClientUploadFile(Map<String, String> filePathMap,String url ) {
        log.info("上传文件filePathMap="+JSON.toJSONString(filePathMap));
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
}
