package com.prs.hub.algorithms.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fanshupeng
 * @create 2023/6/1 10:02
 */
@Getter
@Setter
public class SubmitWorkflowResDTO {
    /**
     * 工作流id
     */
    private String cromwellId;
    /**
     * 结果目录
     */
    private String outputsPath;
}
