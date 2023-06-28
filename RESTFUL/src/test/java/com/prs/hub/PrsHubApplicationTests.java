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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class PrsHubApplicationTests {
    @Autowired
    private SFTPSystemService sftpSystemService;

    @Test
    void contextLoads() throws Exception {

        Map<String,Object> map = new HashMap<>();

        List<String> l = new ArrayList<>();
        l.add("1");
        l.add("2");
        l.add("3");
        map.put("filePathList" ,l);
        JSONObject inputJson = new JSONObject(map);
        List<String> kk = (List<String>)inputJson.get("filePathList");
        System.out.println(inputJson.toJSONString());

    }


}
