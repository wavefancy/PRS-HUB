package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.PrsFile;
import com.prs.hub.practice.mapper.PrsFileMapper;
import com.prs.hub.practice.bo.PrsFileBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-04-13
 */
@Service
public class PrsFileBoImpl extends ServiceImpl<PrsFileMapper, PrsFile> implements PrsFileBo {

}
