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
 * 算法参数表
 * </p>
 *
 * @author fansp
 * @since 2022-03-18
 */
@Getter
@Setter
@TableName("parameter")
@ApiModel(value = "Parameter对象", description = "算法参数表")
public class Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("所属算法id")
    @TableField("algorithms_id")
    private Long algorithmsId;

    @ApiModelProperty("参数名")
    @TableField("NAME")
    private String name;

    @ApiModelProperty("描述 ")
    @TableField("description")
    private String description;

    @ApiModelProperty("默认值")
    @TableField("default_value")
    private String defaultValue;

    @ApiModelProperty("参数校验的正则表达式")
    @TableField("reg_exp")
    private String regExp;

    @ApiModelProperty("错误提示信息")
    @TableField("error_msg")
    private String errorMsg;

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
