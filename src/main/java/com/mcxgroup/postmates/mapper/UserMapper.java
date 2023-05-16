package com.mcxgroup.postmates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcxgroup.postmates.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
