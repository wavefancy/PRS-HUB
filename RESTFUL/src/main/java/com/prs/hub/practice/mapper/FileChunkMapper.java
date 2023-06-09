package com.prs.hub.practice.mapper;

import com.prs.hub.practice.entity.FileChunk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文件分片记录表 Mapper 接口
 * </p>
 *
 * @author fansp
 * @since 2023-06-09
 */
@Mapper
public interface FileChunkMapper extends BaseMapper<FileChunk> {

}
