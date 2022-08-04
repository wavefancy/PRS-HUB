package com.prs.hub.algorithms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2022/3/30 16:07
 */
@Getter
@Setter
public class ParameterEnterReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private String id;

    /**参数名**/
    private String fileId;

    /**默认值**/
    private String value;

    /**参数名**/
    private String name;
    /**工作流uuid**/
    private String workflowExecutionUuid;

}
