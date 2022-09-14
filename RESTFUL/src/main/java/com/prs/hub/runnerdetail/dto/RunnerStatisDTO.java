package com.prs.hub.runnerdetail.dto;

import com.prs.hub.practice.dto.ParameterEnterDTO;
import com.prs.hub.statistics.dto.GWASAndLDFilenameDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/6/17 16:51
 */
@Getter
@Setter
public class RunnerStatisDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long  runnerId ;
    private String  uuid ;
    private String  email ;
    private String  jobName;
    private String   algorithmsName ;
    private Long   algorithmsId ;
    private String   runnerStatus ;
    private String   resultPath;
    private String   runnerProgress;
    private String   runnerQueue;
    private String createdDate;
    private List<ParameterEnterDTO> parameterEnterDTOS;
    private List<GWASAndLDFilenameDTO> gwasAndLDFilenames;

}
