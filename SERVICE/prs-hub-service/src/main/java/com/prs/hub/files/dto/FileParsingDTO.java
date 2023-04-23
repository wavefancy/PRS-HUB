package com.prs.hub.files.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fanshupeng
 * @create 2023/4/21 15:51
 */
@Getter
@Setter
public class FileParsingDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 人种信息
     */
    private String popValue;
    /**
     * 目标文件名
     */
    private String destinationFileName;
}
