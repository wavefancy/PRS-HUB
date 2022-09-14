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
 * 运行job与选择file的关联表
 * </p>
 *
 * @author fansp
 * @since 2022-09-14
 */
@Getter
@Setter
@TableName("runner_detail_to_file")
@ApiModel(value = "RunnerDetailToFile对象", description = "运行job与选择file的关联表")
public class RunnerDetailToFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("runner_id")
    private Long runnerId;

    @ApiModelProperty("file表类型为GWAS的id，值为用户上传")
    @TableField("GWAS_file_id")
    private Long gwasFileId;

    @ApiModelProperty("file表类型为LD的id，值为用户上传")
    @TableField("LD_file_id")
    private Long ldFileId;

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
