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
     * 执行wdl脚本的地址
     */
    private String wdlPath;
    /**
     * 算法id
     */
    private String algorithmsId;
    /**
     * 用户填写的参数数据
     */
    private JSONObject inputJson;
}
