package com.prs.hub.practice.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author fanshupeng
 * @create 2022/3/30 11:10
 */
@Getter
@Setter
public class ParameterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private Long id;

    /**所属算法id**/
    private Long algorithmsId;

    /**参数名**/
    private String name;

    /**描述 **/
    private String description;

    /**默认值**/
    private String defaultValue;

    /**参数校验的正则表达式**/
    private String regExp;

    /**错误提示信息**/
    private String errorMsg;

    private String createdUser;

    private LocalDateTime createdDate;

    private String modifiedUser;

    private LocalDateTime modifiedDate;

    /**0：未删除，1：已删除**/
    private Integer isDelete;

}
