package com.prs.hub.runnerdetailtofile.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author fanshupeng
 * @create 2022/9/14 17:10
 */
@Getter
@Setter
public class RunnerDetailToFileReqDTO implements Serializable {

    private static final long serialVersionUID = 1743L;

    /**主键*/
    private Long id;

    /**"用户id"*/
    private Long runnerId;

    /**"file表类型为GWAS的id，值为用户上传"*/
    private Long gwasFileId;

    /**"file表类型为LD的id，值为用户上传"*/
    private Long ldFileId;

    private String createdUser;

    private LocalDateTime createdDate;

    private String modifiedUser;

    private LocalDateTime modifiedDate;

    /**"0：未删除，1：已删除"*/
    private Integer isDelete;
}
