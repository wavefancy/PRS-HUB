package com.prs.hub.file.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author fanshupeng
 * @create 2022/4/20 15:14
 */
@Getter
@Setter
public class PrsFileReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键**/
    private Long id;

    /**用户id**/
    private Long userId;

    /**路径**/
    private String filePath;

    /**文件名**/
    private String fileName;

    /**后缀**/
    private String fileSuffix;

    /**文件类型**/
    private String fileType;

    /**描述**/
    private String descrition;
    /**
     * 状态
     */
    private String status;
    /**
     * 上传日期
     */
    private String uploadDate;
    /**
     * 失效日期
     */
    private LocalDateTime deleteDate;
    /**
     * 修改日期
     */
    private LocalDateTime modifiedDate;
    /**
     * 创建日期
     */
    private LocalDateTime createdDate;
}
