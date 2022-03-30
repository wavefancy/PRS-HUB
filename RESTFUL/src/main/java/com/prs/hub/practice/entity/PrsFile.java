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
 * 文件表
 * </p>
 *
 * @author fansp
 * @since 2022-03-22
 */
@Getter
@Setter
@TableName("prs_file")
@ApiModel(value = "PrsFile对象", description = "文件表")
public class PrsFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("路径")
    @TableField("file_path")
    private String filePath;

    @ApiModelProperty("文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty("后缀")
    @TableField("file_suffix")
    private String fileSuffix;

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
