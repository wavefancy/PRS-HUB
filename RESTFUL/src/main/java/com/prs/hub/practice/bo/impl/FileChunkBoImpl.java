package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.FileChunk;
import com.prs.hub.practice.mapper.FileChunkMapper;
import com.prs.hub.practice.bo.FileChunkBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件分片记录表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2023-06-09
 */
@Service
public class FileChunkBoImpl extends ServiceImpl<FileChunkMapper, FileChunk> implements FileChunkBo {

}
