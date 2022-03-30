package com.prs.hub.practice.bo.impl;

import com.prs.hub.practice.entity.Menu;
import com.prs.hub.practice.mapper.MenuMapper;
import com.prs.hub.practice.bo.MenuBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author fansp
 * @since 2022-03-18
 */
@Service
public class MenuBoImpl extends ServiceImpl<MenuMapper, Menu> implements MenuBo {

}
