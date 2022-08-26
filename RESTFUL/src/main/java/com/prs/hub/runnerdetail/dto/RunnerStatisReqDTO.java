package com.prs.hub.runnerdetail.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.prs.hub.commons.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2022/7/18 9:48
 */
@Getter
@Setter
public class RunnerStatisReqDTO extends BasePage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    private Long id;

    /**用户id*/
    private Long userId;

    /**上传文件id*/
    private Long fileId;

    /**工作名称*/
    private String jobName;

    /**工作流uuid*/
    private String workflowExecutionUuid;

    /**运行结果文件地址*/
    private String resultPath;

    /**运行进度  100:完成*/
    private Integer progress;

    /**排队位置*/
    private Integer queue;

    /** 运行状态 3:Finish, 2:Project at risk ,1:In progress,0:Not started
     */
    private Integer status;
}
