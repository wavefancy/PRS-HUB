package com.prs.hub.practice.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/3/30 11:09
 */
@Getter
@Setter
public class AlgorithmsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private Long id;

    /**算法名**/
    private String name;

    /**概要**/
    private String summary;

    private String createdUser;

    private LocalDateTime createdDate;

    private String modifiedUser;

    private LocalDateTime modifiedDate;

    /**0：未删除，1：已删除**/
    private Integer isDelete;

    private List<ParameterDTO> parameters;
}
