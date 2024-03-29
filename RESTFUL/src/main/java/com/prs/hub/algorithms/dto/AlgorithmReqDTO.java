package com.prs.hub.algorithms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/30 16:07
 */
@Getter
@Setter
public class AlgorithmReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private String id;

    /**算法名**/
    private String name;

    /**概要**/
    private String summary;
    /**
     * 类型 多个multiple,单一single
     */
    private String type;
    /**
     * 参数
     */
    private List<ParameterEnterReqDTO> parameters;
}
