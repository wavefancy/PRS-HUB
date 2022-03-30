package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.User;
import com.prs.hub.practice.mapper.UserMapper;
import com.prs.hub.practice.bo.UserBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-03-29
 */
@Service
public class UserBoImpl extends ServiceImpl<UserMapper, User> implements UserBo {

}
