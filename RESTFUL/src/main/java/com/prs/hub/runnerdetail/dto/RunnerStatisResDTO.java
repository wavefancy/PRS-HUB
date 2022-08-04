package com.prs.hub.runnerdetail.dto;

import com.prs.hub.practice.dto.ParameterEnterDTO;
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
public class RunnerStatisResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String  uuid ;
    private String  email ;
    private String  fileNameGWAS;
    private String  fileNameLD;
    private String  jobName;
    private String   algorithmsName ;
    private String   runnerStatus ;
    private String   resultPath;
    private String   runnerProgress;
    private String   runnerQueue;
    /**
     * Running状态的排位
     */
    private String   queue;
    private String createdDate;
    private List<ParameterEnterDTO> parameterEnterDTOS;


}
