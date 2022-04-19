package com.prs.hub.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * @author fanshupeng
 * @create 2022/4/19 9:55
 */
public class FileUtil {
    public static void writerJsonFile(String fileName, JSONObject jsonObject){
        BufferedWriter bw = null;
        try{
            File file = new File(fileName);
            if(!file.exists())//判断文件是否存在，若不存在则新建
            {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream=new FileOutputStream(file);//实例化FileOutputStream
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream,"utf-8");//将字符流转
            //实例化对象
            bw = new BufferedWriter(outputStreamWriter);

            //写入数据
            bw.write(jsonObject.toJSONString());
            bw.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(bw !=  null){
                try{
                    bw.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
    }
}
