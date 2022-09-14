package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.RunnerDetailToFile;
import com.prs.hub.practice.mapper.RunnerDetailToFileMapper;
import com.prs.hub.practice.bo.RunnerDetailToFileBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 运行job与选择file的关联表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-09-14
 */
@Service
public class RunnerDetailToFileBoImpl extends ServiceImpl<RunnerDetailToFileMapper, RunnerDetailToFile> implements RunnerDetailToFileBo {

}
