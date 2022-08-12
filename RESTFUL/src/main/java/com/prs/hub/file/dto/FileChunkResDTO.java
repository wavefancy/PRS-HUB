package com.prs.hub.file.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author fanshupeng
 * @create 2022/8/9 10:59
 */

@Getter
@Setter
public class FileChunkResDTO {
    /**
     *
     */
    private Long id;
    /**
     * 添加日期
     */
    private LocalDateTime addTime;
    /**
     * 当前分片，从1开始
     */
    private Integer chunkNumber;
    /**
     * 分片大小
     */
    private Float chunkSize;
    /**
     * 当前分片大小
     */
    private Float currentChunkSize;
    /**
     * 总分片数
     */
    private Integer totalChunks;
    /**
     * 文件标识
     */
    private String identifier;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 相对路径
     */
    private String relativePath;

}
