package com.prs.hub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prs.hub.sftpsystem.service.SFTPSystemService;
import com.prs.hub.utils.CromwellUtil;
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
//        String hello = "admin@163.com/1648106598625/hello.wdl";
//        String inputs = "admin@163.com/1648106190840/inputs.json";
//        File helloFile = sftpSystemService.toFileByPath(hello);
//        File inputsFile = sftpSystemService.toFileByPath(inputs);
//        Map<String, File> filePathMap =  new HashMap<String,File>();
//        filePathMap.put("workflowSource", helloFile);
//        filePathMap.put("workflowInputs",inputsFile);
//        String remote_url = "http://39.103.140.193:8000/api/workflows/v1";// 第三方服务器请求地址
//        HttpClientUtil.httpClientUploadFileByfile(filePathMap,remote_url);
//        Map<String,String> map = new HashMap<String,String>();
//        map.put("status","Succeeded");
////        HashMap<String, Object> resMap = HttpClientUtil.get(map,"http://192.168.118.93:9000/api/workflows/v1/query");
//        JSONObject json = new JSONObject();
//        json.put("id","5e0d56bd-16da-4e44-80be-3e1f8f61658d");
//        HashMap<String, Object> resMap = HttpClientUtil.post(json,"http://192.168.118.93:9000/api/workflows/v1/uuid/status".replace("uuid","5e0d56bd-16da-4e44-80be-3e1f8f61658d"));
//        JSONObject res = (JSONObject) JSON.parse((String) resMap.get("result"));
//        System.out.println(res.toJSONString());
    }


}
