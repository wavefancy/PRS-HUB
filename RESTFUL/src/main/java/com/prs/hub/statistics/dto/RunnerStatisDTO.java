package com.prs.hub.statistics.dto;

import com.prs.hub.practice.dto.ParameterEnterDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author fanshupeng
 * @create 2022/6/17 16:51
 */
@Getter
@Setter
public class RunnerStatisDTO implements Serializable {
    private String  uuid ;
    private String  email ;
    private String  fileName;
    private String  jobName;
    private String   algorithmsName ;
    private String   runnerStatus ;
    private String   resultPath;
    private String   runnerProgress;
    private String   runnerQueue;
    private String createdDate;
    private List<ParameterEnterDTO> parameterEnterDTOS;


}
