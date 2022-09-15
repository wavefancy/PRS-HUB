package com.prs.hub.statistics.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author fanshupeng
 * @create 2022/9/14 11:11
 */
@Getter
@Setter
public class GWASAndLDFilenameDTO implements Serializable {
    private static final long serialVersionUID = 121233L;
    private String gwasFileName;
    private String ldFileName;
    private Long gwasFileId;
    private Long ldFileId;
}
