package com.prs.hub.authentication.service;

import com.prs.hub.commons.BaseResult;
import com.prs.hub.practice.entity.User;


public interface AuthService {
    /**
     * 新增或修改user
     * @param user
     * @return
     */
    BaseResult saveOrUpdateUser(User user);

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    BaseResult getUserInfoById(Long id);

    /**
     * 根据传入信息获取用户信息
     * @param user
     * @return
     */
    BaseResult getUserInfo(User user);

}
