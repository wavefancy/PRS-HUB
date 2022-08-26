package com.prs.hub.practice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author fansp
 * @since 2022-05-15
 */
@Getter
@Setter
@TableName("PRS_USER")
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    @Version
    private Long id;

    @ApiModelProperty("邮箱地址")
    @TableField("email")
    private String email;

    @ApiModelProperty("密码")
    @TableField("PASSWORD")
    private String password;

    @ApiModelProperty("名")
    @TableField("first_name")
    private String firstName;

    @ApiModelProperty("姓")
    @TableField("last_name")
    private String lastName;

    @ApiModelProperty("职称")
    @TableField("job_title")
    private String jobTitle;

    @ApiModelProperty("城市")
    @TableField("city")
    private String city;

    @ApiModelProperty("国家")
    @TableField("country")
    private String country;

    @ApiModelProperty("组织")
    @TableField("organisation")
    private String organisation;

    @ApiModelProperty("性别")
    @TableField("sex")
    private Integer sex;

    @ApiModelProperty("年龄")
    @TableField("age")
    private Integer age;

    @ApiModelProperty("证件号")
    @TableField("id_no")
    private String idNo;

    @ApiModelProperty("手机号")
    @TableField("mobile")
    private String mobile;

    @ApiModelProperty("注册状态 1:已激活,0:未激活")
    @TableField("status")
    private Integer status;

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
