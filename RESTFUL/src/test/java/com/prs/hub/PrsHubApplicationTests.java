package com.prs.hub;

import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.utils.HttpClientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class PrsHubApplicationTests {
    @Autowired
    private SFTPSystemService sftpSystemService;

    @Test
    void contextLoads() throws Exception {
        String hello = "admin@163.com/1648106598625/hello.wdl";
        String inputs = "admin@163.com/1648106190840/inputs.json";
        File helloFile = sftpSystemService.toFileByPath(hello);
        File inputsFile = sftpSystemService.toFileByPath(inputs);
        Map<String, File> filePathMap =  new HashMap<String,File>();
        filePathMap.put("workflowSource", helloFile);
        filePathMap.put("workflowInputs",inputsFile);
        String remote_url = "http://39.103.140.193:8000/api/workflows/v1";// 第三方服务器请求地址
        HttpClientUtil.httpClientUploadFileByfile(filePathMap,remote_url);

    }


}
