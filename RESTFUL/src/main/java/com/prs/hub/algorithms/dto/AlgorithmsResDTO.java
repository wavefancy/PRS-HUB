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
public class AlgorithmsResDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private Long id;

    /**算法名**/
    private String name;

    /**概要**/
    private String summary;
    /**
     * 参数
     */
    private List<ParameterResDTO> parameters;
}
