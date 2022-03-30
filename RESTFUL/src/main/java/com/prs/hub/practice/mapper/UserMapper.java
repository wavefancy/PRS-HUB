package com.prs.hub.practice.mapper;

import com.prs.hub.practice.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author fansp
 * @since 2022-03-29
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
