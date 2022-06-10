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
 * 跑数据详情表
 * </p>
 *
 * @author fansp
 * @since 2022-06-10
 */
@Getter
@Setter
@TableName("runner_detail")
@ApiModel(value = "RunnerDetail对象", description = "跑数据详情表")
public class RunnerDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId("id")
    private Long id;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("上传文件id")
    @TableField("file_id")
    private Long fileId;

    @ApiModelProperty("工作流uuid")
    @TableField("workflow_execution_uuid")
    private String workflowExecutionUuid;

    @ApiModelProperty("运行结果文件地址")
    @TableField("result_path")
    private String resultPath;

    @ApiModelProperty("运行进度  100:完成")
    @TableField("progress")
    private Integer progress;

    @ApiModelProperty("排队位置")
    @TableField("queue")
    private Integer queue;

    @ApiModelProperty("运行状态 4:Finish, 3:Project at risk ,1:In progress,0:Not started")
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
