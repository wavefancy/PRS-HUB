package com.prs.hub.practice.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2022/6/23 14:41
 */
@Getter
@Setter
public class ParameterEnterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long fileId;

    private Long algorithmsId;

    /**
     * 参数名
     */
    private String parameterName;

    /**
     * 录入的参数
     */
    private String parameterValue;
}
