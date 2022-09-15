package com.prs.hub.algorithms.dto;

import com.prs.hub.statistics.dto.GWASAndLDFilenameDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/4/1 16:25
 */
@Setter
@Getter
public class AlgorithmsReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long fileGWASId;
    private List<AlgorithmReqDTO> algorithmList;
    private Long fileLDId;
    private List<GWASAndLDFilenameDTO> gwasAndLDFilenameDTOList;
    //工作名称
    private String jobName;
}
