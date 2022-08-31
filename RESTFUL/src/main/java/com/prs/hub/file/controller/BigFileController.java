package com.prs.hub.file.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.prs.hub.authentication.dto.UserReqDTO;
import com.prs.hub.commons.Authorization;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.commons.CurrentUser;
import com.prs.hub.commons.JsonResult;
import com.prs.hub.constant.MessageEnum;
import com.prs.hub.file.dto.FileChunkReqDTO;
import com.prs.hub.file.dto.FileChunkResDTO;
import com.prs.hub.file.service.BigFileService;
import com.prs.hub.file.service.FileChunkService;
import com.prs.hub.utils.FileUtil;
import com.prs.hub.utils.MultipartFileToFileUtil;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fanshupeng
 * @create 2022/8/8 15:54
 */
@Slf4j
@RestController
@RequestMapping(value = "/prs/hub/bigfile")
public class BigFileController {
    @Autowired
    private BigFileService bigFileService;
    @Autowired
    private FileChunkService fileChunkService;
    @Value("${gwas.title}")
    private String gwasTitle;


    /**
     * 上传校验
     * @param param
     * @return
     */
    @Authorization
    @GetMapping("/upload")
    public JsonResult<Map<String, Object>> checkUpload(@Valid FileChunkReqDTO param) {
        log.info("文件MD5:" + param.getIdentifier());

        List<FileChunkResDTO> list = fileChunkService.listByFileMd5(param.getIdentifier());

        Map<String, Object> data = new HashMap<>(1);


        if (list.size() == 0) {
            data.put("uploaded", false);
            return JsonResult.ok(data);
        }
        // 处理单文件
        if (list.get(0).getTotalChunks() == 1) {
            data.put("uploaded", true);
            // todo 返回 url
            data.put("url", "");
            return JsonResult.ok(data);
        }
        // 处理分片
        int[] uploadedFiles = new int[list.size()];
        int index = 0;
        for (FileChunkResDTO fileChunkItem : list) {
            uploadedFiles[index] = fileChunkItem.getChunkNumber();
            index++;
        }
        data.put("uploadedChunks", uploadedFiles);
        return JsonResult.ok(data);
    }

    /**
     * 上传文件
     * @param param
     * @return
     */
    @Authorization
    @PostMapping("/upload")
    public JsonResult<Map<String, Object>> chunkUpload(@CurrentUser UserReqDTO userReqDTO,  @Valid FileChunkReqDTO param, HttpServletRequest req) {

        String email = userReqDTO.getEmail();
        log.info("上传文件email="+email);
        log.info("上传文件fileNameInput="+param.getFileNameInput());
        log.info("上传文件identifier="+param.getIdentifier());
        log.info("上传文件ChunkNumber="+param.getChunkNumber());

        if(StringUtils.isEmpty(param.getFileNameInput())){
            log.info("未录入文件名");
            return JsonResult.error(MessageEnum.FAIL);
        }
        Map<String,Object> resMap = bigFileService.uploadFile(email,param);
        Boolean flag = (Boolean)resMap.get("flag");
        if (!flag) {
            return JsonResult.error(MessageEnum.FAIL);
        }
        resMap.put("identifier",param.getIdentifier());
        return JsonResult.ok(resMap);
    }

    /**
     * 校验文件内容的title信息
     * @param req
     * @return
     */
    @Authorization
    @RequestMapping(value = "/checkFileTitle",method = RequestMethod.POST)
    public JsonResult<Map<String, Object>> uploadFiles( HttpServletRequest req ){
        String filePath = req.getParameter("filePath");
        log.info("上传文件存储路径filePath="+filePath);
        String fileName = req.getParameter("fileName");
        log.info("上传文件存储名fileName="+fileName);
        String suffixName = req.getParameter("suffixName");
        log.info("上传文件后缀suffixName="+suffixName);
        String identifier = req.getParameter("identifier");
        log.info("上传文件唯一标识identifier="+identifier);

        if(StringUtils.isEmpty(filePath)||StringUtils.isEmpty(fileName)||StringUtils.isEmpty(suffixName)||StringUtils.isEmpty(identifier)){
            return JsonResult.error(500,"信息为空");
        }
        String fullFileName = filePath+fileName+suffixName;
        log.info("校验文件内容的title信息fullFileName="+ fullFileName);

        String headers = FileUtil.getGZIPDataHeaderByFileName(fullFileName);;
        log.info("获取文件文本的header="+headers);

        //将定义的title填充到map里面用于校验
        HashMap<String,Boolean> gwasTitleMap = new HashMap<>();
        String[] gwasTitleS = gwasTitle.split(",");
        for (String title:gwasTitleS) {
            gwasTitleMap.put(title,false);
        }

        //遍历header逐一校验
        String[] headerArr = headers.split("\t");
        for (String header : headerArr) {
            boolean has = gwasTitleMap.containsKey(header);
            if(has){
                gwasTitleMap.put(header,has);
            }
        }

        //提取每一项校验结果
        String errorStr = "";//未包含的title项
        for (String key :gwasTitleMap.keySet()) {
            if(!gwasTitleMap.get(key)){
                if(StringUtils.isEmpty(errorStr)){
                    errorStr += key;
                }else{
                    errorStr += ","+key;
                }
            }
        }
        log.info("文件缺少errorStr="+errorStr);
        HashMap<String,Object> resMap = new HashMap<>();
        boolean flag = true;
        if(StringUtils.isNotEmpty(errorStr)){
            flag = false;
            resMap.put("msg","文件缺少"+errorStr+"头部信息");

            //删除文件分片信息
            FileChunkReqDTO fileChunkReqDTO = new FileChunkReqDTO();
            fileChunkReqDTO.setIdentifier(identifier);
            log.info("删除文件分片信息fileChunkReqDTO="+JSON.toJSONString(fileChunkReqDTO));
            Boolean deleteFileChunk = fileChunkService.deleteFileChunk(fileChunkReqDTO);
            log.info("删除文件分片信息deleteFileChunk="+JSON.toJSONString(deleteFileChunk));
            //删除存储的文件
            File delteFile = new File(fullFileName);
            Boolean delteFlag = MultipartFileToFileUtil.delteTempFile(delteFile);
            log.info("删除文件delteFlag="+delteFlag);
        }
        resMap.put("flag",flag);
        log.info("校验结束resMap="+JSON.toJSONString(resMap));
        return JsonResult.ok(resMap);
    }
}
