package com.prs.hub.practice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 
 * </p>
 *
 * @author fansp
 * @since 2022-05-15
 */
@Getter
@Setter
@TableName("METADATA_ENTRY")
@ApiModel(value = "MetadataEntry对象", description = "")
public class MetadataEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "METADATA_JOURNAL_ID", type = IdType.AUTO)
    private Long metadataJournalId;

    @TableField("WORKFLOW_EXECUTION_UUID")
    private String workflowExecutionUuid;

    @TableField("METADATA_KEY")
    private String metadataKey;

    @TableField("CALL_FQN")
    private String callFqn;

    @TableField("JOB_SCATTER_INDEX")
    private Integer jobScatterIndex;

    @TableField("JOB_RETRY_ATTEMPT")
    private Integer jobRetryAttempt;

    @TableField("METADATA_VALUE")
    private String metadataValue;

    @TableField("METADATA_TIMESTAMP")
    private LocalDateTime metadataTimestamp;

    @TableField("METADATA_VALUE_TYPE")
    private String metadataValueType;


}
