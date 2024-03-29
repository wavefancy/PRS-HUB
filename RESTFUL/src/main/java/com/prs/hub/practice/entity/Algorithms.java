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
 * 算法表
 * </p>
 *
 * @author fansp
 * @since 2023-05-31
 */
@Getter
@Setter
@TableName("algorithms")
@ApiModel(value = "Algorithms对象", description = "算法表")
public class Algorithms implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("算法名")
    @TableField("NAME")
    private String name;

    @ApiModelProperty("概要")
    @TableField("summary")
    private String summary;

    @ApiModelProperty("固定参数")
    @TableField("fixed_parameter")
    private String fixedParameter;

    @ApiModelProperty("wdl脚本地址")
    @TableField("wdl_path")
    private String wdlPath;

    @ApiModelProperty("options模板地址")
    @TableField("options_path")
    private String optionsPath;

    @ApiModelProperty("类型：多个multiple,单一single")
    @TableField("type")
    private String type;

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
