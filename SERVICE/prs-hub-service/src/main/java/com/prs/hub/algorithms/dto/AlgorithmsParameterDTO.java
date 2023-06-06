package com.prs.hub.algorithms.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fanshupeng
 * @create 2023/4/13 15:26
 */
@Getter
@Setter
public class AlgorithmsParameterDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * job的id
     */
    private String runnerId;
    /**
     * 执行wdl脚本文件的地址
     */
    private String wdlPath;
    /**
     * option数据文件的地址
     */
    private String optionsPath;
    /**
     * 算法id
     */
    private String algorithmsId;
    /**
     * 用户填写的参数数据
     */
    private JSONObject inputJson;
}
