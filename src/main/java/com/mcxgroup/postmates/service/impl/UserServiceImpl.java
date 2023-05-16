package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.entity.User;
import com.mcxgroup.postmates.mapper.UserMapper;
import com.mcxgroup.postmates.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description: 用户服务层
 * @author: MCXEN
 * @date: 2022/11/29
 * MCXEN
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
