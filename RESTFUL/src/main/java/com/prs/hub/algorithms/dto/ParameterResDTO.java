package com.prs.hub.algorithms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2022/3/30 16:07
 */
@Getter
@Setter
public class ParameterResDTO implements Serializable {

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

}
