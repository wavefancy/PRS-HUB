package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.prs.hub.algorithms.dto.AlgorithmResDTO;
import com.prs.hub.algorithms.dto.ParameterResDTO;
import com.prs.hub.algorithms.service.AlgorithmsService;
import com.prs.hub.practice.bo.AlgorithmsBo;
import com.prs.hub.practice.bo.AlgorithmsSpecialBo;
import com.prs.hub.practice.dto.AlgorithmsDTO;
import com.prs.hub.practice.dto.ParameterDTO;
import com.prs.hub.practice.entity.Algorithms;
import com.prs.hub.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 算法service
 * @author fanshupeng
 * @create 2022/3/25 16:12
 */
@Slf4j
@Service
public class AlgorithmsServiceImpl implements AlgorithmsService {
    @Autowired
    private AlgorithmsSpecialBo algorithmsSpecialBo;
    @Autowired
    private AlgorithmsBo algorithmsBo;

    @Override
    public List<AlgorithmResDTO> queryAlgorithmsDetails() {
        log.info("service查询算法详细信息开始");
        log.info("service调用Bo查询算法详细信息开始");
//        List<AlgorithmsDTO> algorithmsDTOList = algorithmsSpecialBo.queryAlgorithmsDetails();
        QueryWrapper<Algorithms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        List<Algorithms> algorithmsList = algorithmsBo.list(queryWrapper);
        log.info("service调用Bo查询算法详细信息结束algorithmsList="+ JSON.toJSONString(algorithmsList));
        List<AlgorithmResDTO> algorithmResDTOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(algorithmsList)){
            for (Algorithms algorithms:algorithmsList) {
                AlgorithmResDTO algorithmResDTO = new AlgorithmResDTO();
                BeanUtils.copyProperties(algorithms, algorithmResDTO);
                List<ParameterResDTO> parameterResDTOList = new ArrayList<>();
                String algorithmName = algorithmResDTO.getName();

                //input文件固定值
                String fixedParameter = algorithms.getFixedParameter();
                /**
                 * 组装返回数据
                 */
                JSONObject jsonObject = null;
                if (StringUtils.isNotEmpty(fixedParameter)){
                    jsonObject = JSON.parseObject(fixedParameter);
                    Set<Map.Entry<String, Object>> jsonSet  = jsonObject.entrySet();
                    int i = 0;
                    for ( Map.Entry entry :jsonSet) {
                        ParameterResDTO parameterResDTO = new ParameterResDTO();
                        String key = entry.getKey().toString();
                        if(key.indexOf("-1-value" )!= -1){
                            String name = key.replace("-1-value","").replace(algorithmName+".","");
                            parameterResDTO.setName(name);
                            if(entry.getValue()!=null){
                                parameterResDTO.setDefaultValue(entry.getValue().toString().replace("[","").replace("]",""));
                            }
                            parameterResDTO.setId(Integer.toUnsignedLong(i));
                            i++;
                            parameterResDTO.setDescription(jsonObject.get(algorithmName+"."+name+"-1-description")!=null?jsonObject.get(algorithmName+"."+name+"-1-description").toString():"");
                            parameterResDTOList.add(parameterResDTO);
                        }
                    }
                }
//                for (ParameterDTO parameterDTO:algorithmsDTO.getParameters()) {
//                    ParameterResDTO parameterResDTO = new ParameterResDTO();
//                    BeanUtils.copyProperties(parameterDTO,parameterResDTO);
//                    parameterResDTOList.add(parameterResDTO);
//                }
                algorithmResDTO.setParameters(parameterResDTOList);
                algorithmResDTOList.add(algorithmResDTO);
                System.out.println();
            }
        }
        log.info("service查询算法详细信息结束algorithmsResDTOList="+JSON.toJSONString(algorithmResDTOList));
        return algorithmResDTOList;
    }
    /**
     * 根据id查询算法
     * @param id
     * @return
     */
    @Override
    public Algorithms queryAlgorithmsById(Long id) {
        log.info("根据id查询算法service开始id="+id);
        Algorithms algorithms = null;
        try {
            algorithms = algorithmsBo.getById(id);
            log.info("根据id查询算法service结束algorithms="+JSON.toJSONString(algorithms));
        }catch (Exception e){
            log.error("根据id查询算法service异常",e);
            return null;
        }
        return algorithms;
    }

}
