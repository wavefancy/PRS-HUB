package com.prs.hub.practice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户录入参数表
 * </p>
 *
 * @author fansp
 * @since 2022-03-22
 */
@Getter
@Setter
@TableName("parameter_enter")
@ApiModel(value = "ParameterEnter对象", description = "用户录入参数表")
public class ParameterEnter implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("id")
    @TableField("file_id")
    private Long fileId;

    @ApiModelProperty("所属算参数d")
    @TableField("parameter_id")
    private Long parameterId;

    @ApiModelProperty("录入的参数")
    @TableField("value")
    private String value;

    @TableField("created_user")
    private String createdUser;

    @TableField("created_date")
    private LocalDateTime createdDate;

    @TableField("modified_user")
    private String modifiedUser;

    @TableField("modified_date")
    private LocalDateTime modifiedDate;

    @ApiModelProperty("0：未删除，1：已删除")
    @TableField("is_delete")
    private Integer isDelete;


}
