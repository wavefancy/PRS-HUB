package com.prs.hub.algorithms.service.impl;

import com.alibaba.fastjson.JSON;
import com.prs.hub.algorithms.service.ParameterService;
import com.prs.hub.practice.bo.ParameterBo;
import com.prs.hub.practice.entity.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanshupeng
 * @create 2022/3/31 19:11
 */
@Slf4j
@Service
public class ParameterServiceImpl implements ParameterService {
    @Autowired
    private ParameterBo parameterBo;
    /**
     * 根据id查询算法参数
     * @param id
     * @return
     */
    @Override
    public Parameter queryParameterById(Long id) {
        log.info("根据id查询算法参数service开始id="+id);
        Parameter parameter = null;
        try {
            parameter = parameterBo.getById(id);
            log.info("根据id查询算法参数service结束parameter="+ JSON.toJSONString(parameter));
        }catch (Exception e){
            log.error("根据id查询算法参数service异常",e);
            return null;
        }
        return parameter;
    }
}
