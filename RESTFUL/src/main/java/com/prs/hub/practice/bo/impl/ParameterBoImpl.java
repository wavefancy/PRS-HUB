package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.Parameter;
import com.prs.hub.practice.mapper.ParameterMapper;
import com.prs.hub.practice.bo.ParameterBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 算法参数表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-03-18
 */
@Service
public class ParameterBoImpl extends ServiceImpl<ParameterMapper, Parameter> implements ParameterBo {

}
