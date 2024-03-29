package com.prs.hub.authentication.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.prs.hub.authentication.service.AuthService;
import com.prs.hub.commons.BaseResult;
import com.prs.hub.practice.bo.UserBo;
import com.prs.hub.practice.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserBo userBo;

    /**
     * 新增或修改用户信息
     * @param user
     * @return
     */
    @Override
    public BaseResult saveOrUpdateUser(User user) {
        log.info("新增或修改用户信息Service开始user="+JSON.toJSONString(user));
        Boolean flag = false;
        try {
            Long useriId = user.getId();
            if(useriId == null){
                user.setCreatedUser("system");
                user.setCreatedDate(LocalDateTime.now());
                user.setIsDelete(0);
            }
            user.setModifiedUser("system");
            user.setModifiedDate(LocalDateTime.now());
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>();
            updateWrapper.eq("id",user.getId());
            flag = userBo.saveOrUpdate(user,updateWrapper);
        }catch (Exception e){
            log.error("新增或修改用户信息异常"+e.getMessage());
            return BaseResult.error("新增或修改用户信息异常");
        }
        log.info("新增或修改用户信息Service结束flag="+flag);
        return BaseResult.ok("新增或修改用户信息成功",flag);
    }

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    @Override
    public BaseResult getUserInfoById(Long id) {
        log.info("根据id获取用户信息Service开始id="+id);
        User user = null;
        try{
            user = userBo.getById(id);
        }catch (Exception e){
            log.error("根据id获取用户信息Service异常"+e.getMessage());
            return BaseResult.error("根据id获取用户信息异常");
        }
        log.info("根据id获取用户信息Service结束user="+JSON.toJSONString(user));

        return BaseResult.ok("根据id获取用户信息成功",user);
    }

    /**
     * 根据传入信息获取用户信息
     * @param user
     * @return
     */
    @Override
    public BaseResult getUserInfo(User user) {
        log.info("根据传入信息获取用户信息开始user="+JSON.toJSONString(user));
        User userOne = null;
        try {
            Wrapper<User> queryWrapper = new QueryWrapper<>(user);
            userOne = userBo.getOne(queryWrapper);
        }catch (Exception e){
            log.error("根据传入信息获取用户信息异常"+e.getMessage());
            return BaseResult.error("根据传入信息获取用户信息异常");
        }
        log.info("根据传入信息获取用户信息结束userOne="+JSON.toJSONString(userOne));
        return BaseResult.ok("根据传入信息获取用户信息成功",userOne);
    }

    @Override
    public BaseResult deleteUser(User user) {
        log.info("删除用户Service开始user="+JSON.toJSONString(user));
        Boolean flag = false;
        try {
            flag =  userBo.removeById(user);
        }catch (Exception e){
            log.error("删除用户异常"+e.getMessage());
            return BaseResult.error("删除用户异常");
        }
        log.info("删除用户Service结束flag="+flag);
        return BaseResult.ok("删除用户成功",flag);
    }

    /**
     * 统计用户个数
     * @return
     */
    @Override
    public BaseResult userCount() {
        log.info("统计用户个数");
         Long count = 0L;
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_delete",0);
            count =  userBo.count(queryWrapper);
        }catch (Exception e){
            log.error("统计用户个数异常"+e.getMessage());
            return BaseResult.error("统计用户个数异常");
        }
        log.info("统计用户个数count="+count);
        return BaseResult.ok("统计用户个数",count);
    }

}
